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

import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.util.plugin.SolrCoreAware;
import org.drools.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.InputStreamResource;

import com.plugtree.solradvert.core.AdvertQuery;

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

  @Override
  public void prepare(ResponseBuilder rb) throws IOException {    
    SolrParams params = rb.req.getParams();
    
    if(!params.getBool(ADVERT_COMPONENT_NAME, false)) {
      return;
    }
    
    logger.debug("Preparing Advert Component...");

    // by wrapping the query with an AdvertQuery, we introduce
    // some useful methods, like "hasTerm", "boost", etc.
    AdvertQuery aq = new AdvertQuery(rb);
    
    // get the knowledge session using Spring
    String rules = params.get(ADVERT_RULES, ADVERT_DEFAULT_RULES);
    try {
      if(params.getBool(ADVERT_RELOAD_RULES, false)) {
        logger.info("Reloading Spring context...");
        reloadContext(rb.req.getCore());
      }
      StatelessKnowledgeSession ksession = (StatelessKnowledgeSession)kcontext.getBean(rules);
      ksession.execute(aq);
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
    return "$URL: https://github.com/Salaboy/Solr-Advert/raw/master/solr-advert/src/main/java/com/plugtree/solradvert/AdvertComponent.java $";
  }

  @Override
  public String getVersion() {
    return "1.0-SNAPSHOT";
  }

}
