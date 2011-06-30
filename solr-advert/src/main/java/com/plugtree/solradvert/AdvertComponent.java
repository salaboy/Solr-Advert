package com.plugtree.solradvert;

/**
 *  Copyright 2011 Plugtree LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.search.Query;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.util.plugin.SolrCoreAware;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.InputStreamResource;

import com.plugtree.solradvert.core.AdvertQuery;
import com.plugtree.solradvert.core.AdvertQueryImpl;
import com.plugtree.solradvert.core.QueryFactsCollector;
import com.plugtree.solradvert.core.SchemaTool;

/**
 * WARNING! This component must be put after the QueryComponent
 * in the components chain.
 * 
 * @author salaboy
 */
public class AdvertComponent extends SearchComponent implements AdvertParams, SolrCoreAware {

  private static Logger logger = LoggerFactory.getLogger(AdvertComponent.class);
  
  private ApplicationContext kcontext;
  
  private String kcontextFile;
  
  @Override
  public void init(@SuppressWarnings("rawtypes") NamedList args) {
    super.init(args);
    
    kcontextFile = (String)args.get(ADVERT_KNOWLEDGE_CONTEXT);
    if(kcontextFile==null) {
      kcontextFile = ADVERT_DEFAULT_KCONTEXT;
    }
  }
  
  @Override
  public void inform(SolrCore core) {
    reloadContext(core);
  }
  
  private void reloadContext(SolrCore core) {
    try {
      logger.info("Loading bean definitions from: " + kcontextFile);
      InputStream input = core.getResourceLoader().openResource(kcontextFile);
      if(input!=null) {
        GenericApplicationContext context = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
        reader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
        reader.loadBeanDefinitions(new InputStreamResource(input));
        context.refresh();
        this.kcontext = context;
      } else {
        logger.error("Bean definitions file not found.");
      }
    } catch(Exception ex) {
      logger.error("Error while reading Spring context.", ex);
    }
  }
  
  private Collection<Object> getFacts(ResponseBuilder rb) {
    Collection<Object> facts = new ArrayList<Object>();
//    ParentChildRelationshipFactory relationshipFactory = ParentChildRelationshipFactory.getInstance();
    QueryFactsCollector factsCollector = new QueryFactsCollector();
    
    // put the main query
    if(rb.getQuery()!=null) {
//      MainQuery mq = new MainQuery(rb.getQuery());
//      facts.add(mq);
      
      // put all the components and relationships of the main query
//      for(Query q: mq) {
//        facts.add(q);
        
//        Collection<ParentChildRelationship> relationships = relationshipFactory.getRelationships(q);
//        if(relationships!=null) {
//          facts.addAll(relationships);
//        }
//      }
      
      factsCollector.collect(rb.getQuery(), facts);
    }
    
    // put all filter queries
    if(rb.getFilters()!=null) {
      for(Query fq: rb.getFilters()) {
//        FilterQuery fqFact = new FilterQuery(fq);
//        facts.add(fqFact);
//        
//        // put all the components and relationships of the filter queries
//        for(Query q: fqFact) {
//          facts.add(q);
//          
//          Collection<ParentChildRelationship> relationships = relationshipFactory.getRelationships(q);
//          if(relationships!=null) {
//            facts.addAll(relationships);
//          }
//        }
        factsCollector.collect(fq, facts);
      }
    }
    
    // put the AdvertQuery
    // this is only for backwards-compatibility, so old tests don't fail
    AdvertQuery aq = new AdvertQueryImpl(rb);
    facts.add(aq);
    
    // put the SchemaTool
    SchemaTool st = new SchemaTool(rb);
    facts.add(st);
    
    // put the response builder
    facts.add(rb);
    
    logger.debug("Collected facts: " + facts);
    
    return facts;
  }

  @Override
  public void prepare(final ResponseBuilder rb) throws IOException {    
    SolrParams params = rb.req.getParams();
    
    if(!params.getBool(ADVERT_COMPONENT_NAME, false)) {
      return;
    }
    
    logger.debug("Preparing Advert Component...");

    // get the knowledge session using Spring
    String rules = params.get(ADVERT_RULES, ADVERT_DEFAULT_RULES);
    try {
      if(params.getBool(ADVERT_RELOAD_RULES, false)) {
        logger.info("Reloading Spring context...");
        reloadContext(rb.req.getCore());
      }
      StatelessKnowledgeSession ksession = (StatelessKnowledgeSession)kcontext.getBean(rules);
      List<Command<?>> cmds = new ArrayList<Command<?>>();
      Collection<?> facts = getFacts(rb);
      cmds.add(CommandFactory.newInsertElements(facts));
      if(params.get(ADVERT_BATCH)!=null) {
        String extraCmdsBean = params.get(ADVERT_BATCH);
        List<Command<?>> extraCmds = (List<Command<?>>)kcontext.getBean(extraCmdsBean);
        logger.debug("Adding " + extraCmds.size() + " extra command(s)");
        cmds.addAll(extraCmds);
      }
      Command<?> batchCmd = CommandFactory.newBatchExecution(cmds);
      logger.debug("Executing Drools session");
      ksession.execute(batchCmd);
    } catch(Exception ex) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, ex);
    }
  }

  @Override
  public void process(ResponseBuilder rb) throws IOException {

  }
  
  @Override
  public String getName() {
    return "Solr Advert Component";
  }

  @Override
  public String getDescription() {
    return "This component allows dynamic boosting and sorting based on Drools rules.";
  }

  @Override
  public String getSourceId() {
    return "";
  }

  @Override
  public String getSource() {
    return "$URL: https://github.com/Salaboy/Solr-Advert/blob/queryIterator/solr-advert/src/main/java/com/plugtree/solradvert/AdvertComponent.java $";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

}
