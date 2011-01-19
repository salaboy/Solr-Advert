/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import java.io.IOException;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author salaboy
 */
public class AdvertComponent extends SearchComponent {
    
    
    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        SolrParams params = rb.req.getParams();
        String q = params.get(CommonParams.Q);
        
        
        StatefulKnowledgeSession ksession = DroolsService.getInstance().getKnowledgeSession("advert");
        ksession.insert(q);
        
        
        
        
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
