package com.plugtree.solradvert;

public interface AdvertParams {
  
  public static final String COMPONENT_NAME = "advert";
  
  public static final String ADVERT_PREFIX = COMPONENT_NAME + ".";
  
  /**
   * The name of the file with the definition of the rules.
   * The value of this parameter is configured in solrconfig.xml
   * The default value is "advert.drl"
   */
  public static final String ADVERT_RULES = ADVERT_PREFIX + "rules";

  public static final String ADVERT_DEFAULT_RULES = "ksession1";
  
  public static final String ADVERT_DEFAULT_KCONTEXT = "knowledge-service.xml";
  
  public static final String ADVERT_KNOWLEDGE_CONTEXT = "kcontext";
}
