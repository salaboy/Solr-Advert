package com.plugtree.solradvert.fact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

public class DismaxParentChildRelationship extends ParentChildRelationship {
  
  private DisjunctionMaxQuery parent;
  
  private Query child;
  
  public DismaxParentChildRelationship(DisjunctionMaxQuery parent, Query child) {
    super(parent, child);
    this.parent = parent;
    this.child = child;
  }

  @Override
  public void remove() {
    Iterator<Query> it = parent.iterator();
    
    while(it.hasNext()) {
      Query q = it.next();
      if(q.equals(child)) {
        it.remove();
        return;
      }
    }
  }
  
  public static class DismaxParentChildRelationshipFactory extends ParentChildRelationshipFactory {
    @Override
    public Collection<ParentChildRelationship> getRelationships(Query parent) {
      
      if(parent instanceof DisjunctionMaxQuery) {
        DisjunctionMaxQuery dismaxQuery = (DisjunctionMaxQuery)parent;
        Collection<ParentChildRelationship> relationships = new ArrayList<ParentChildRelationship>();
        
        for(Query child: dismaxQuery) {
          relationships.add(new DismaxParentChildRelationship(dismaxQuery, child));
        }
        return relationships;
      }
      
      return null;
      
    }
  }

}
