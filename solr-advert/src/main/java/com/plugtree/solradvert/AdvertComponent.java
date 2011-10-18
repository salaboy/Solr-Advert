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
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plugtree.solradvert.core.AdvertQuery;
import com.plugtree.solradvert.core.AdvertQueryImpl;
import com.plugtree.solradvert.core.QueryFactsCollector;
import com.plugtree.solradvert.core.SchemaTool;
import com.plugtree.solradvert.core.SolrXmlApplicationContext;

/**
 * WARNING! This component must be put after the QueryComponent
 * in the components chain.
 * 
 * @author salaboy
 */
public class AdvertComponent extends SearchComponent implements AdvertParams, SolrCoreAware {

  private static Logger logger = LoggerFactory.getLogger(AdvertComponent.class);
  
  private SolrXmlApplicationContext kcontext;
  
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
    // NOTE: ecj can't be used because it conflicts with the version
    // included in Jetty
    System.setProperty("drools.dialect.java.compiler", "JANINO");
    
    loadKContext(core);
  }
  
  private void loadKContext(SolrCore core) {
    try {
      logger.info("Loading bean definitions from: " + kcontextFile);
      kcontext = new SolrXmlApplicationContext(core, kcontextFile);
      kcontext.refresh();
    } catch(Exception ex) {
      throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, ex);
    }
  }
  
  private Collection<Object> getFacts(ResponseBuilder rb) {
    Collection<Object> facts = new ArrayList<Object>();
    QueryFactsCollector factsCollector = new QueryFactsCollector();
    
    // put the main query
    if(rb.getQuery()!=null) {
      factsCollector.collect(rb.getQuery(), facts);
    }
    
    // put all the filter queries
    if(rb.getFilters()!=null) {
      for(Query fq: rb.getFilters()) {
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

    try {
      // if advert.reload=true ---> reload spring's context
      if(params.getBool(ADVERT_RELOAD_RULES, false)) {
        logger.info("Reloading Spring context...");
        if(kcontext!=null) {
          kcontext.refresh();
        } else {
          loadKContext(rb.req.getCore());
        }
      }
      
      String rules = params.get(ADVERT_RULES, ADVERT_DEFAULT_RULES);
      KnowledgeBase kbase = (KnowledgeBase) kcontext.getBean(rules);
      StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
      
      List<Command<?>> cmds = new ArrayList<Command<?>>();
      
      if(params.get(ADVERT_BATCH)!=null) {
        String extraCmdsBean = params.get(ADVERT_BATCH);
        List<Command<?>> extraCmds = (List<Command<?>>)kcontext.getBean(extraCmdsBean);
        cmds.addAll(extraCmds);
        logger.debug("Added " + extraCmds.size() + " extra command(s) to batch execution");
      }
      
      Collection<?> facts = getFacts(rb);
      cmds.add(CommandFactory.newInsertElements(facts));
      
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
