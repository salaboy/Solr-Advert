/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import java.io.InputStream;
import java.util.Collection;

import org.apache.solr.core.SolrResourceLoader;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author salaboy
 */
public class DroolsService {
  private static DroolsService service;
  private static KnowledgeBase kbase;
  private static Logger logger = LoggerFactory.getLogger(DroolsService.class);

  public static DroolsService getInstance() {
    if (service == null) {
      service = new DroolsService();
    }
    return service;
  }

  private DroolsService() {
  }

  private void createKnowledgeBase(String rules) {
    if (DroolsService.kbase == null) {
      KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
      SolrResourceLoader loader = new SolrResourceLoader(null);
      InputStream is = loader.openConfig(rules);
      Resource resource = ResourceFactory.newInputStreamResource(is);
      kbuilder.add(resource, ResourceType.DRL);
      KnowledgeBuilderErrors errors = kbuilder.getErrors();
      if (errors.size() > 0) {
          for (KnowledgeBuilderError error : errors) {
              logger.warn("Error while compiling rule: " + error);
          }
//          throw new IllegalArgumentException("Could not parse knowledge.");
      }

      DroolsService.kbase = KnowledgeBaseFactory.newKnowledgeBase();
      Collection<KnowledgePackage> kpackages = kbuilder.getKnowledgePackages();
      logger.debug(getRulesCount(kpackages) + " rules found");
      DroolsService.kbase.addKnowledgePackages(kpackages);
    }

  }
  
  private int getRulesCount(Collection<KnowledgePackage> kpackages) {
    int count = 0;
    for(KnowledgePackage kpackage: kpackages) {
      count += kpackage.getRules().size();
    }
    return count;
  }

  public StatefulKnowledgeSession getKnowledgeSession(String name) {
    // the same component should be able to use different sets of rules,
    // depending on a parameter of the request handler or the request itself
    // maybe we can introduce something like a "knowledge base cache" here?
    createKnowledgeBase(name);
    return DroolsService.kbase.newStatefulKnowledgeSession();
  }

}
