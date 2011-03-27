/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import java.io.IOException;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.drools.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.plugtree.solradvert.core.AdvertQuery;

/**
 * WARNING! This component must be put after the QueryComponent
 * in the components chain.
 * 
 * @author salaboy
 */
public class AdvertComponent extends SearchComponent implements AdvertParams {

  private static Logger logger = LoggerFactory.getLogger(AdvertComponent.class);
  
  private ApplicationContext kcontext;
  
  @Override
  public void init(@SuppressWarnings("rawtypes") NamedList args) {
    super.init(args);
    
    // initialize the Spring context
    
    String kcontextFile = (String)args.get(ADVERT_KNOWLEDGE_CONTEXT);
    if(kcontextFile==null) {
      kcontextFile = ADVERT_DEFAULT_KCONTEXT;
    }
    
    kcontext = new ClassPathXmlApplicationContext(kcontextFile);
  }

  @Override
  public void prepare(ResponseBuilder rb) throws IOException {    
    SolrParams params = rb.req.getParams();
    
    if(!params.getBool(COMPONENT_NAME, false)) {
      return;
    }
    
    logger.debug("Preparing Advert Component...");

    // by wrapping the query with an AdvertQuery, we introduce
    // some useful methods, like "hasTerm", "boost", etc.
    AdvertQuery aq = new AdvertQuery(rb);
    
    // get the knowledge session using Spring
    String rules = params.get(ADVERT_RULES, ADVERT_DEFAULT_RULES);
    try {
      StatelessKnowledgeSession ksession = (StatelessKnowledgeSession)kcontext.getBean(rules);
      ksession.execute(aq);
    } catch(Exception ex) {
      logger.error("Error while trying to execute knowledge session.", ex);
    }
  }

  @Override
  public void process(ResponseBuilder rb) throws IOException {

  }

  @Override
  public String getDescription() {
    return "Advert Component!";
  }

  @Override
  public String getSourceId() {
    return "V1";
  }

  @Override
  public String getSource() {
    return "";
  }

  @Override
  public String getVersion() {
    return "V1";
  }

}
