package com.plugtree.solradvert;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plugtree.solradvert.fact.DefaultParentChildRelationshipFactory;
import com.plugtree.solradvert.fact.ParentChildRelationship;
import com.plugtree.solradvert.fact.ParentChildRelationshipFactory;
import com.plugtree.solradvert.util.AbstractAdvertTestCase;
import com.plugtree.solradvert.util.SolrTest;

public class ParentChildRelationshipTest extends AbstractAdvertTestCase {
  
  private static final Logger LOG = LoggerFactory.getLogger(ParentChildRelationshipTest.class);
  
  @Test
  public void testBooleanQuery() throws Exception {
    Query[] qq = new Query[] {
        new TermQuery(new Term("description", "coffe")),
        new TermQuery(new Term("description", "tea")),
        new TermQuery(new Term("description", "mate")),
    };
    
    BooleanQuery q = new BooleanQuery();
    for(Query termQuery: qq) {
      q.add(termQuery, Occur.SHOULD);
    }
    
    ParentChildRelationshipFactory factory = new DefaultParentChildRelationshipFactory();
    Collection<ParentChildRelationship> rels = factory.getRelationships(q);
    
    assertEquals(qq.length, rels.size());
    
    int i=0;
    for(ParentChildRelationship rel: rels) {
      assertEquals(q, rel.getParent());
      assertEquals(qq[i++], rel.getChild());
    }
  }
  
  @Test
  public void testDismaxQuery() throws Exception {
    Query[] qq = new Query[] {
        new TermQuery(new Term("description", "coffe")),
        new TermQuery(new Term("description", "tea")),
        new TermQuery(new Term("description", "mate")),
    };
    DisjunctionMaxQuery q = new DisjunctionMaxQuery(0);
    
    for(Query termQuery: qq) {
      q.add(termQuery);
    }
    
    ParentChildRelationshipFactory factory = new DefaultParentChildRelationshipFactory();
    Collection<ParentChildRelationship> rels = factory.getRelationships(q);
    
    assertEquals(qq.length, rels.size());
    
    int i=0;
    for(ParentChildRelationship rel: rels) {
      assertEquals(q, rel.getParent());
      assertEquals(qq[i++], rel.getChild());
    }
  }

}
