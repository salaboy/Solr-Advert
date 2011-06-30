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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.search.FunctionQParserPlugin;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QueryParsing;
import org.apache.solr.search.SortSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvertQueryImpl implements AdvertQuery {
	
	private static Logger logger = LoggerFactory.getLogger(AdvertQueryImpl.class);
	
	private ResponseBuilder rb;
	
	private Query q;
	
	private Collection<Query> fqs;
	
	public AdvertQueryImpl(ResponseBuilder rb) {
		this.rb = rb;
		this.q = rb.getQuery();
		this.fqs = rb.getFilters();
	}
	
	/* (non-Javadoc)
   * @see com.plugtree.solradvert.core.AdvertQuery#boost(java.lang.String)
   */
	@Override
  public void boost(String qstr) {
		logger.debug("Adding boost query: " + qstr);
		try {
			QParser qparser = QParser.getParser(qstr, FunctionQParserPlugin.NAME, rb.req);
			Query qq = qparser.parse();
			
			BooleanQuery newq = new BooleanQuery();
			newq.add(new BooleanClause(q, Occur.MUST));
			newq.add(new BooleanClause(qq, Occur.SHOULD));
			
			rb.setQuery(newq);
		} catch(ParseException ex) {
			logger.error("Error while adding boost query: " + ex);
		}
	}
	
	/* (non-Javadoc)
   * @see com.plugtree.solradvert.core.AdvertQuery#setSort(java.lang.String)
   */
	@Override
  public void setSort(String sortSpec) {
	  logger.debug("New sort specification: " + sortSpec);
	  Sort newSort = QueryParsing.parseSort(sortSpec, rb.req);
	  int offset = rb.getSortSpec().getOffset();
	  int count = rb.getSortSpec().getCount();
	  rb.setSortSpec(new SortSpec(newSort, offset, count));
	}
	
	@Override
	public void addFilter(String qstr) {
	  logger.debug("Adding filter: " + qstr);
	  try {
	    QParser qparser = QParser.getParser(qstr, null, rb.req);
	    Query q = qparser.parse();
	    
	    List<Query> fqs = rb.getFilters();
	    if(fqs==null) {
	      fqs = new ArrayList<Query>();
	      rb.setFilters(fqs);
	    }
	    
	    fqs.add(q);
	  } catch(ParseException ex) {
	    logger.error("Error while adding filter query", ex);
	  }
	}

}
