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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.apache.lucene.index.Term;
import org.junit.Test;

import com.plugtree.solradvert.core.HasTermQueryVisitor;
import com.plugtree.solradvert.util.AbstractAdvertTestCase;
import com.plugtree.solradvert.util.SolrTest;

public class VisitorsTest extends AbstractAdvertTestCase {
  
  @Test
  @SolrTest
  public void testHasTerm() throws Exception {
    Term term = new Term("description", "solr");
    HasTermQueryVisitor visitor = new HasTermQueryVisitor(term);
    
    assertFalse(visitor.visit(getQuery("description:lucene")));
    assertTrue(visitor.visit(getQuery("description:solr")));
    assertFalse(visitor.visit(getQuery("brand:solr")));
    assertTrue(visitor.visit(getQuery("description:lucene description:solr")));
    assertFalse(visitor.visit(getQuery("description:lucene brand:solr")));
    assertFalse(visitor.visit(getQuery("*:*")));
  }  

}
