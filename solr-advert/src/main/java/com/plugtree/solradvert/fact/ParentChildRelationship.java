package com.plugtree.solradvert.fact;

import org.apache.lucene.search.Query;

public abstract class ParentChildRelationship {
  
  protected Query parent;
  
  protected Query child;
  
  public ParentChildRelationship(Query parent, Query child) {
    this.parent = parent;
    this.child = child;
  }

  public Query getParent() {
    return parent;
  }

  public void setParent(Query parent) {
    this.parent = parent;
  }

  public Query getChild() {
    return child;
  }

  public void setChild(Query child) {
    this.child = child;
  }
  
  public abstract void remove();

}
