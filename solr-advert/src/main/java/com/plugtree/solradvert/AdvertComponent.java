/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import java.io.IOException;

import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
public class AdvertComponent extends SearchComponent {
    
	private static Logger logger = LoggerFactory.getLogger(AdvertComponent.class);
    
    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
    	logger.debug("Preparing Advert Component...");
    	
    	ModifiableSolrParams modifiableParams = new ModifiableSolrParams(rb.req.getParams());
    	
        String q = modifiableParams.get(CommonParams.Q);
        
        
//        StatefulKnowledgeSession ksession = DroolsService.getInstance().getKnowledgeSession("advert");
//        ksession.insert(q);
        
        //boosting those documents created by 'user2'
        modifiableParams.set(DisMaxParams.BQ, "(author:user2)^2");
        
        rb.req.setParams(modifiableParams);
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
