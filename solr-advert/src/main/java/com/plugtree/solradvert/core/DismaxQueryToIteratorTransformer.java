package com.plugtree.solradvert.core;

import java.util.Iterator;

import org.apache.commons.collections.Transformer;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

public class DismaxQueryToIteratorTransformer extends QueryToIteratorTransformer {
  
  public DismaxQueryToIteratorTransformer(Transformer next) {
    super(next);
  }

  @Override
  public Iterator<Query> iterator(Query q) {
    if(!(q instanceof DisjunctionMaxQuery)) {
      return null;
    }
    
    DisjunctionMaxQuery qq = (DisjunctionMaxQuery)q;
    return qq.iterator();
  }

}
