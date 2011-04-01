package com.plugtree.solradvert;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import com.plugtree.solradvert.core.AdvertQuery;

public class SolrDslTest {
  
  private KnowledgeBase kbase;
  
  public void initKBase(String rulesFile) {
    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    kbuilder.add(new ClassPathResource("solr/conf/solr.dsl"), ResourceType.DSL);
    kbuilder.add(new ClassPathResource("solr/conf/solr-test.dsl"), ResourceType.DSL);
    kbuilder.add(new ClassPathResource(rulesFile), ResourceType.DSLR);
    
    kbase = kbuilder.newKnowledgeBase();
  }
  
  private StatefulKnowledgeSession newSession() {
    return kbase.newStatefulKnowledgeSession();
  }
  
  @Test
  public void testHasTerm() {
    AdvertQuery q = createMock(AdvertQuery.class);
    expect(q.hasTerm("description", "solr")).andReturn(false).andReturn(true);
    replay(q);
    
    initKBase("rules/testHasTerm.dslr");
    
    StatefulKnowledgeSession ksession = newSession();
    ksession.insert(q);
    ksession.fireAllRules();
    assertEquals(1, ksession.getObjects().size());
    ksession.dispose();
    
    ksession = newSession();
    ksession.insert(q);
    ksession.fireAllRules();
    assertEquals(0, ksession.getObjects().size());
    ksession.dispose();
    
    verify(q);
  }
  
  @Test
  public void testBoost() {
    AdvertQuery q = createMock(AdvertQuery.class);
    q.boost("name:solr");
    replay(q);
    
    initKBase("rules/testBoost.dslr");
    
    StatefulKnowledgeSession ksession = newSession();
    ksession.insert(q);
    ksession.fireAllRules();
    ksession.dispose();
    
    verify(q);
  }
  
  @Test
  public void testSetSort() {
    AdvertQuery q = createMock(AdvertQuery.class);
    q.setSort("price asc");
    replay(q);
    
    initKBase("rules/testSetSort.dslr");
    
    StatefulKnowledgeSession ksession = newSession();
    ksession.insert(q);
    ksession.fireAllRules();
    ksession.dispose();
    
    verify(q);
  }

}
