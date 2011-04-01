package com.plugtree.solradvert;

/**
 *  Copyright 2011 Plugtree LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public interface AdvertParams {
  
  public static final String ADVERT_COMPONENT_NAME = "advert";
  
  public static final String ADVERT_PREFIX = ADVERT_COMPONENT_NAME + ".";
  
  /**
   * The name of the file with the definition of the rules.
   * The value of this parameter is configured in solrconfig.xml
   * The default value is "advert.drl"
   */
  public static final String ADVERT_RULES = ADVERT_PREFIX + "rules";

  public static final String ADVERT_DEFAULT_RULES = "ksession1";
  
  public static final String ADVERT_DEFAULT_KCONTEXT = "solr/conf/knowledge-service.xml";
  
  public static final String ADVERT_KNOWLEDGE_CONTEXT = "kcontext";
  
  public static final String ADVERT_RELOAD_RULES = ADVERT_PREFIX + "reload";
}
