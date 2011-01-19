/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 *
 * @author salaboy
 */
public class DroolsService {
    private static DroolsService service;
    private static KnowledgeBase kbase;
    public static DroolsService getInstance(){
        if(service == null){
            service = new DroolsService();
        }
        return service;
    }

    private DroolsService() {}
    
    private void createKnowledgeBase(String rules){
        if(DroolsService.kbase == null){
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(ResourceFactory.newFileResource(rules), ResourceType.DRL);
            //check for errors during compilation

            DroolsService.kbase = KnowledgeBaseFactory.newKnowledgeBase();
            DroolsService.kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        }
                
    }

    public StatefulKnowledgeSession getKnowledgeSession(String name) {
        createKnowledgeBase(name+".drl");
        return DroolsService.kbase.newStatefulKnowledgeSession();
    }
    
    
    
    

}
