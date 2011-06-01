package com.plugtree.solradvert;

import java.util.Iterator;

import org.junit.Assert;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.junit.Test;

import com.plugtree.solradvert.core.DefaultQueryIteratorFactory;
import com.plugtree.solradvert.util.AbstractAdvertTestCase;

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

public class IteratorsTest extends AbstractAdvertTestCase {
  
  private DefaultQueryIteratorFactory factory = new DefaultQueryIteratorFactory();
  
  @Test
  public void testTermQuery() {
    TermQuery q = new TermQuery(new Term("foo", "bar"));
    Iterator<Query> it = factory.iterator(q);
    
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals(q, it.next());
    Assert.assertFalse(it.hasNext());
  }
  
  @Test
  public void testBooleanQuery() {
    TermQuery q1 = new TermQuery(new Term("foo", "bar"));
    TermQuery q2 = new TermQuery(new Term("bar", "foo"));
    
    BooleanQuery q = new BooleanQuery();
    q.add(q1, Occur.SHOULD);
    q.add(q2, Occur.SHOULD);
    
    Iterator<Query> it = factory.iterator(q);
    
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals(q1, it.next());
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals(q2, it.next());
    Assert.assertFalse(it.hasNext());
  }
  
  @Test
  public void testDismaxQuery() {
    TermQuery q1 = new TermQuery(new Term("foo", "bar"));
    TermQuery q2 = new TermQuery(new Term("bar", "foo"));
    
    DisjunctionMaxQuery q = new DisjunctionMaxQuery(0.5f);
    q.add(q1);
    q.add(q2);
    
    Iterator<Query> it = factory.iterator(q);
    
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals(q1, it.next());
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals(q2, it.next());
    Assert.assertFalse(it.hasNext());
  }

}
