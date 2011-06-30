package com.plugtree.solradvert.fact;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class BooleanParentChildRelationship extends ParentChildRelationship {
  
  private BooleanQuery parent;
  
  private BooleanClause childClause;
  
  public BooleanParentChildRelationship(BooleanQuery parent, BooleanClause childClause) {
    super(parent, childClause.getQuery());
    this.parent = parent;
    this.childClause = childClause;
  }

  @Override
  public void remove() {
    parent.clauses().remove(childClause);
  }
  
  public static class BooleanParentChildRelationshipFactory extends ParentChildRelationshipFactory {
    @Override
    public Collection<ParentChildRelationship> getRelationships(Query parent) {
      
      if(parent instanceof BooleanQuery) {
        BooleanQuery booleanQuery = (BooleanQuery)parent;
        Collection<ParentChildRelationship> relationships = new ArrayList<ParentChildRelationship>();
        
        for(BooleanClause clause: booleanQuery.clauses()) {
          relationships.add(new BooleanParentChildRelationship(booleanQuery, clause));
        }
        
        return relationships;
      }
      
      return null;
    }
  }

}
