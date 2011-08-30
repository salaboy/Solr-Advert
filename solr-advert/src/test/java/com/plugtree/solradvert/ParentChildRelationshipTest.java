package com.plugtree.solradvert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.plugtree.solradvert.fact.BooleanParentChildRelationship;
import com.plugtree.solradvert.fact.DefaultParentChildRelationshipFactory;
import com.plugtree.solradvert.fact.DismaxParentChildRelationship;
import com.plugtree.solradvert.fact.ParentChildRelationship;
import com.plugtree.solradvert.fact.ParentChildRelationshipFactory;
import com.plugtree.solradvert.util.AbstractAdvertTestCase;

public class ParentChildRelationshipTest extends AbstractAdvertTestCase {
  
  private static final Logger LOG = LoggerFactory.getLogger(ParentChildRelationshipTest.class);
  
  @Test
  public void testEqualsAndHashCode() throws Exception {
    TermQuery[] termQ = new TermQuery[] {
        new TermQuery(new Term("description", "coffe")),
        new TermQuery(new Term("description", "tea")),
    };
    
    BooleanClause[] clauses = new BooleanClause[] {
        new BooleanClause(termQ[0], Occur.SHOULD),
        new BooleanClause(termQ[1], Occur.SHOULD),
    };
    
    DisjunctionMaxQuery dismaxQ = new DisjunctionMaxQuery(0);
    dismaxQ.add(termQ[0]);
    dismaxQ.add(termQ[1]);
    
    BooleanQuery booleanQ = new BooleanQuery();
    booleanQ.add(clauses[0]);
    booleanQ.add(clauses[1]);
    
    ParentChildRelationship dismaxRel = new DismaxParentChildRelationship(dismaxQ, termQ[0]);
    ParentChildRelationship booleanRel = new BooleanParentChildRelationship(booleanQ, clauses[0]);
    
    assertTrue(dismaxRel.equals(dismaxRel));
    assertEquals(dismaxRel.hashCode(), dismaxRel.hashCode());
    assertTrue(booleanRel.equals(booleanRel));
    assertEquals(booleanRel.hashCode(), booleanRel.hashCode());
    
    assertFalse(dismaxRel.equals(booleanRel));
    assertFalse(booleanRel.equals(dismaxRel));
    
    assertTrue(dismaxRel.equals(new DismaxParentChildRelationship(dismaxQ, termQ[0])));
    assertEquals(dismaxRel.hashCode(), new DismaxParentChildRelationship(dismaxQ, termQ[0]).hashCode());
    assertFalse(dismaxRel.equals(new DismaxParentChildRelationship(dismaxQ, termQ[1])));
    
    assertTrue(booleanRel.equals(new BooleanParentChildRelationship(booleanQ, clauses[0])));
    assertEquals(booleanRel.hashCode(), new BooleanParentChildRelationship(booleanQ, clauses[0]).hashCode());
    assertFalse(booleanRel.equals(new BooleanParentChildRelationship(booleanQ, clauses[1])));
  }
  
  @Test
  public void testBooleanRelFactory() throws Exception {
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
      assertEquals(BooleanParentChildRelationship.class, rel.getClass());
      assertEquals(q, rel.getParent());
      assertEquals(qq[i++], rel.getChild());
    }
  }
  
  @Test
  public void testBooleanRelGettersAndSetters() throws Exception {
    BooleanQuery parentQ = new BooleanQuery();
    TermQuery childQ = new TermQuery(new Term("description", "foo"));
    parentQ.add(childQ, Occur.SHOULD);
    
    BooleanParentChildRelationship rel = new BooleanParentChildRelationship(parentQ, new BooleanClause(childQ, Occur.SHOULD));
    assertEquals(parentQ, rel.getParent());
    assertEquals(childQ, rel.getChild());
  }
  
  @Test
  public void testBooleanRelDelete() throws Exception {
    BooleanQuery parentQ = new BooleanQuery();
    TermQuery childQ = new TermQuery(new Term("description", "foo"));
    parentQ.add(childQ, Occur.SHOULD);
    
    BooleanParentChildRelationship rel = new BooleanParentChildRelationship(parentQ, new BooleanClause(childQ, Occur.SHOULD));
    assertEquals(1, parentQ.getClauses().length);
    rel.remove();
    assertEquals(0, parentQ.getClauses().length);
    
    // removing a relationship many times shouldn't fail
    rel.remove();
    assertEquals(0, parentQ.getClauses().length);
  }
  
  @Test
  public void testDismaxRelFactory() throws Exception {
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
      assertEquals(DismaxParentChildRelationship.class, rel.getClass());
      assertEquals(q, rel.getParent());
      assertEquals(qq[i++], rel.getChild());
    }
  }
  
  @Test
  public void testDismaxRelGettersAndSetters() throws Exception {
    DisjunctionMaxQuery parentQ = new DisjunctionMaxQuery(0);
    TermQuery childQ = new TermQuery(new Term("description", "foo"));
    parentQ.add(childQ);
    
    DismaxParentChildRelationship rel = new DismaxParentChildRelationship(parentQ, childQ);
    assertEquals(parentQ, rel.getParent());
    assertEquals(childQ, rel.getChild());
  }
  
  @Test
  public void testDismaxRelDelete() throws Exception {
    DisjunctionMaxQuery parentQ = new DisjunctionMaxQuery(0);
    TermQuery childQ = new TermQuery(new Term("description", "foo"));
    parentQ.add(childQ);
    
    DismaxParentChildRelationship rel = new DismaxParentChildRelationship(parentQ, childQ);
    assertEquals(true, parentQ.iterator().hasNext());
    rel.remove();
    assertEquals(false, parentQ.iterator().hasNext());
    
    // removing a relationship many times shouldn't fail
    rel.remove();
    assertEquals(false, parentQ.iterator().hasNext());
  }

}
