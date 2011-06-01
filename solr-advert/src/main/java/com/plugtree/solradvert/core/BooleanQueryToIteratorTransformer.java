package com.plugtree.solradvert.core;

import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.Transformer;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class BooleanQueryToIteratorTransformer extends QueryToIteratorTransformer {
  
  public BooleanQueryToIteratorTransformer(Transformer next) {
    super(next);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Query> iterator(Query q) {
    if(!(q instanceof BooleanQuery)) {
      return null;
    }
    
    BooleanQuery qq = (BooleanQuery)q;
    return IteratorUtils.transformedIterator(
        IteratorUtils.arrayIterator(qq.getClauses()), 
        new Transformer() {
          @Override
          public Object transform(Object clause) {
            return ((BooleanClause)clause).getQuery();
          }
        });
  }

}
