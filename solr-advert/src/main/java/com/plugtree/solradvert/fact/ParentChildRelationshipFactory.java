package com.plugtree.solradvert.fact;

import java.util.Collection;

import org.apache.lucene.search.Query;


public abstract class ParentChildRelationshipFactory {
  
  private static ParentChildRelationshipFactory INSTANCE;
  
  public static ParentChildRelationshipFactory getInstance() {
    if(INSTANCE==null) {
      INSTANCE = new DefaultParentChildRelationshipFactory();
    }
    return INSTANCE;
  }
  
  public abstract Collection<ParentChildRelationship> getRelationships(Query parent);

}
