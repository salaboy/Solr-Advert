package com.plugtree.solradvert.fact;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

public class DefaultParentChildRelationshipFactory extends ParentChildRelationshipFactory {
  
  private Map<Class<?>, ParentChildRelationshipFactory> factories;
  
  public DefaultParentChildRelationshipFactory() {
    factories = new HashMap<Class<?>, ParentChildRelationshipFactory>();
    factories.put(
        BooleanQuery.class, 
        new BooleanParentChildRelationship.BooleanParentChildRelationshipFactory());
    factories.put(
        DisjunctionMaxQuery.class, 
        new DismaxParentChildRelationship.DismaxParentChildRelationshipFactory());
  }
  
  public Collection<ParentChildRelationship> getRelationships(Query parent) {
    ParentChildRelationshipFactory factory = factories.get(parent.getClass());
    if(factory!=null) {
      return factory.getRelationships(parent);
    }
    return null;
  }

}
