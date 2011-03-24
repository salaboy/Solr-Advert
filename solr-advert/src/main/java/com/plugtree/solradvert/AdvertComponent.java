/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import java.io.IOException;

import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plugtree.solradvert.core.AdvertQuery;

/**
 * WARNING! This component must be put after the QueryComponent
 * in the components chain.
 * 
 * @author salaboy
 */
public class AdvertComponent extends SearchComponent implements AdvertParams {

  private static Logger logger = LoggerFactory.getLogger(AdvertComponent.class);

  @Override
  public void prepare(ResponseBuilder rb) throws IOException {
    logger.debug("Preparing Advert Component...");

    // by wrapping the query with an AdvertQuery, we introduce
    // some useful methods, like "hasTerm", "boost", etc.
    AdvertQuery aq = new AdvertQuery(rb);
    
    // get the knowledge session
    SolrParams params = rb.req.getParams();
    String rules = params.get(ADVERT_RULES, ADVERT_DEFAULT_RULES);
    StatefulKnowledgeSession ksession = DroolsService.getInstance().getKnowledgeSession(rules);
    
    ksession.insert(aq);
    
    ksession.fireAllRules();
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
