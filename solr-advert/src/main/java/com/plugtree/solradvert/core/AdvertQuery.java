package com.plugtree.solradvert.core;

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

import org.apache.lucene.search.Query;
import org.apache.solr.search.FunctionQParserPlugin;

/**
 * The query that will be inserted in the KnowledgeSession. It's a
 * wrapper of {@link Query} that introduces a set of useful methods
 * for rules writing.
 * 
 * @author jgrande
 *
 */
public interface AdvertQuery {

  /**
   * Add the score returned by the query <code>qstr</code>
   * to the original score of each document. By default,
   * <code>qstr</code> is parsed with {@link FunctionQParserPlugin}, but
   * this can be overridden using local params, eg:
   * <code>{!lucene}field:text</code>.
   * 
   * @param qstr the query string to use 
   */
  public void boost(String qstr);

  /**
   * Replace the sort specification given in the <code>sort</code> request
   * parameter by the specification given in the <code>sortSpec</code> parameter
   * of this function.
   * 
   * @param sortSpec the sort specification to use
   */
  public void setSort(String sortSpec);

}