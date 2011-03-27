package com.plugtree.solradvert;

import java.util.Date;

import org.apache.solr.util.AbstractSolrTestCase;

public abstract class AbstractAdvertTestCase extends AbstractSolrTestCase {

  protected void addDoc(String id, String product, String brand, String description, Date date, Double price) {
    assertU(adoc("id", id, "product", product, "brand", brand, "description", description, "price", price.toString()));
  }

}
