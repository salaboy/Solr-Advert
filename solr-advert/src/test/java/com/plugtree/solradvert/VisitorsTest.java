package com.plugtree.solradvert;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.apache.lucene.index.Term;
import org.junit.Test;

import com.plugtree.solradvert.core.HasTermQueryVisitor;

public class VisitorsTest extends AbstractAdvertTestCase {
  
  @Override
  public String getSchemaFile() {
    return "solr/conf/schema.xml";
  }
  
  @Override
  public String getSolrConfigFile() {
    return "solr/conf/solrconfig.xml";
  }
  
  @Test
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
