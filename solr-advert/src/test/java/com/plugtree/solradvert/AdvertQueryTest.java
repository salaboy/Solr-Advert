package com.plugtree.solradvert;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.apache.lucene.search.Query;
import org.apache.solr.handler.component.ResponseBuilder;
import org.easymock.EasyMock;
import org.junit.Test;

import com.plugtree.solradvert.core.AdvertQuery;
import com.plugtree.solradvert.core.AdvertQueryImpl;
import com.plugtree.solradvert.util.AbstractAdvertTestCase;
import com.plugtree.solradvert.util.SolrTest;

public class AdvertQueryTest extends AbstractAdvertTestCase {
  
  @Test
  @SolrTest
  public void testHasTermInFilter() throws Exception {
    Query q = getQuery("*:*");
    
    List<Query> fqs = new LinkedList<Query>();
    fqs.add(getQuery("brand:nike"));
    fqs.add(getQuery("product:shoes"));
    
    ResponseBuilder rb = createMock(ResponseBuilder.class);
    EasyMock.expect(rb.getQuery()).andReturn(q).anyTimes();
    EasyMock.expect(rb.getFilters()).andReturn(fqs).anyTimes();
    
    replayAllMocks();
    
    AdvertQuery advertQ = new AdvertQueryImpl(rb);
    
    Assert.assertFalse(advertQ.hasTermInFilter("brand", "adidas"));
    Assert.assertTrue(advertQ.hasTermInFilter("brand", "nike"));
    Assert.assertTrue(advertQ.hasTermInFilter("product", "shoes"));
  }
  
  @Test
  @SolrTest
  public void testHasTermLuceneQParser() throws Exception {    
    ResponseBuilder rb = createMock(ResponseBuilder.class);
    EasyMock.expect(rb.getQuery()).andReturn(getQuery("product:shoes brand:adidas"));
    EasyMock.expect(rb.getFilters()).andReturn(new LinkedList<Query>());
    
    replayAllMocks();
    
    AdvertQuery advertQ = new AdvertQueryImpl(rb);
    
    Assert.assertFalse(advertQ.hasTerm("description", "running"));
    Assert.assertTrue(advertQ.hasTerm("product", "shoes"));
    Assert.assertTrue(advertQ.hasTerm("brand", "adidas"));
  }
  
  @Test
  @SolrTest
  public void testHasTermDismaxQParser() throws Exception {    
    ResponseBuilder rb = createMock(ResponseBuilder.class);
    EasyMock.expect(rb.getQuery()).andReturn(getQuery("{!dismax qf=\"product description\"}running"));
    EasyMock.expect(rb.getFilters()).andReturn(new LinkedList<Query>());
    
    replayAllMocks();
    
    AdvertQuery advertQ = new AdvertQueryImpl(rb);
    
    Assert.assertFalse(advertQ.hasTerm("brand", "running"));
    Assert.assertTrue(advertQ.hasTerm("product", "running"));
    Assert.assertTrue(advertQ.hasTerm("description", "run"));
  }

}
