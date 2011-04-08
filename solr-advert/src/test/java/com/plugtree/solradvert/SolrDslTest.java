package com.plugtree.solradvert;

/**
 *      Copyright 2011 Plugtree LLC
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
  public void testFilterHasTerm() {
    AdvertQuery q = createMock(AdvertQuery.class);
    expect(q.hasTermInFilter("description", "solr")).andReturn(false).andReturn(true);
    replay(q);
    
    initKBase("rules/testHasTermInFilter.dslr");
    
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
