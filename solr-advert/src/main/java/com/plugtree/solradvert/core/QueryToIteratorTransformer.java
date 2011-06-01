package com.plugtree.solradvert.core;

import java.util.Iterator;

import org.apache.commons.collections.Transformer;
import org.apache.lucene.search.Query;

public abstract class QueryToIteratorTransformer implements Transformer {
  
  private Transformer next;
  
  public QueryToIteratorTransformer(Transformer next) {
    this.next = next;
  }

  @Override
  public final Object transform(Object q) {
    try {
      Iterator<Query> it = iterator((Query)q);
      if(it!=null) {
        return it;
      }
    } catch(ClassCastException ex) {
      throw new IllegalArgumentException("Expected org.apache.lucene.search.Query but found " + q.getClass().getName());
    }
  
    
    return next!=null ? next.transform(q) : q;
  }
  
  public abstract Iterator<Query> iterator(Query q);

}
