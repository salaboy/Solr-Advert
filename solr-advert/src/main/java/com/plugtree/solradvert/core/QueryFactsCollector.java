package com.plugtree.solradvert.core;

import java.util.Collection;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

import com.plugtree.solradvert.fact.BooleanParentChildRelationship;
import com.plugtree.solradvert.fact.DismaxParentChildRelationship;

public class QueryFactsCollector {
  
  public void collect(Query q, Collection<Object> facts) {
    if(BooleanQuery.class.equals(q.getClass())) {
      collect((BooleanQuery)q, facts);
    } else if(DisjunctionMaxQuery.class.equals(q.getClass())) {
      collect((DisjunctionMaxQuery)q, facts);
    }
    
    facts.add(q);
  }
  
  public void collect(BooleanQuery q, Collection<Object> facts) {
    for(BooleanClause clause: q.getClauses()) {
      facts.add(new BooleanParentChildRelationship(q, clause));
      collect(clause.getQuery(), facts);
    }
  }
  
  public void collect(DisjunctionMaxQuery q, Collection<Object> facts) {
    for(Query qq: q) {
      facts.add(new DismaxParentChildRelationship(q, qq));
      collect(qq, facts);
    }
  }

}
