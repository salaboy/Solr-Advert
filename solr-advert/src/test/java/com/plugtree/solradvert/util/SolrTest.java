package com.plugtree.solradvert.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SolrTest {
  
  String solrConfig() default "solr/conf/solrconfig.xml";
  String schema()     default "solr/conf/schema.xml";

}
