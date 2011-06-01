package com.plugtree.solradvert.core;

import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;
import org.apache.lucene.search.Query;

public class DefaultQueryIteratorFactory {
  
  private Transformer chain;
  
  public DefaultQueryIteratorFactory() {
    chain = new BooleanQueryToIteratorTransformer(null);
    chain = new DismaxQueryToIteratorTransformer(chain);
    
  }

  @SuppressWarnings("unchecked")
  public Iterator<Query> iterator(Query q) {
    return IteratorUtils.objectGraphIterator(q, chain);
  }

}
