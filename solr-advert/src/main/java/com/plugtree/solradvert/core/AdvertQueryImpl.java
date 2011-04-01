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

import org.apache.lucene.index.Term;
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
	
	public AdvertQueryImpl(ResponseBuilder rb) {
		this.rb = rb;
		this.q = rb.getQuery();
	}
	
	/* (non-Javadoc)
   * @see com.plugtree.solradvert.core.AdvertQuery#hasTerm(java.lang.String, java.lang.String)
   */
	@Override
  public boolean hasTerm(String field, String text) {
		Term term = new Term(field, text);
		HasTermQueryVisitor queryVisitor = new HasTermQueryVisitor(term);
		return queryVisitor.visit(this.q);
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
			
		}
	}
	
	/* (non-Javadoc)
   * @see com.plugtree.solradvert.core.AdvertQuery#setSort(java.lang.String)
   */
	@Override
  public void setSort(String sortSpec) {
	  logger.debug("New sort specification: " + sortSpec);
	  Sort newSort = QueryParsing.parseSort(sortSpec, rb.req.getSchema());
	  int offset = rb.getSortSpec().getOffset();
	  int count = rb.getSortSpec().getCount();
	  rb.setSortSpec(new SortSpec(newSort, offset, count));
	}

}
