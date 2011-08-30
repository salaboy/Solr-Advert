package com.plugtree.solradvert;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import com.plugtree.solradvert.core.QueryFactsCollector;
import com.plugtree.solradvert.fact.BooleanParentChildRelationship;
import com.plugtree.solradvert.fact.DismaxParentChildRelationship;
import com.plugtree.solradvert.util.AbstractAdvertTestCase;

public class QueryFactsCollectorTest extends AbstractAdvertTestCase {
  
  @Test
  public void testBooleanQuery() throws Exception {
    BooleanQuery q = new BooleanQuery();
    q.add(new TermQuery(new Term("description", "a")), Occur.SHOULD);
    q.add(new TermQuery(new Term("description", "b")), Occur.SHOULD);
    q.add(new TermQuery(new Term("description", "c")), Occur.SHOULD);
    
    List<Object> facts = new ArrayList<Object>();
    QueryFactsCollector factsCollector = new QueryFactsCollector();
    factsCollector.collect(q, facts);
    
    assertEquals(6, facts.size());
    for(BooleanClause clause: q.getClauses()) {
      Query qq = clause.getQuery();
      assertTrue(facts.contains(qq));
      assertTrue(facts.contains(new BooleanParentChildRelationship(q, clause)));
    }
  }
  
  @Test
  public void testDismaxQuery() throws Exception {
    DisjunctionMaxQuery q = new DisjunctionMaxQuery(0);
    q.add(new TermQuery(new Term("description", "a")));
    q.add(new TermQuery(new Term("description", "b")));
    q.add(new TermQuery(new Term("description", "c")));
    
    List<Object> facts = new ArrayList<Object>();
    QueryFactsCollector factsCollector = new QueryFactsCollector();
    factsCollector.collect(q, facts);
    
    assertEquals(6, facts.size());
    for(Query qq: q) {
      assertTrue(facts.contains(qq));
      assertTrue(facts.contains(new DismaxParentChildRelationship(q, qq)));
    }
  }

}
