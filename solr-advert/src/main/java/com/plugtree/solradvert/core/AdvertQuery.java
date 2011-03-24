package com.plugtree.solradvert.core;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.search.FunctionQParserPlugin;
import org.apache.solr.search.QParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The query that will be inserted in the KnowledgeSession. It's a
 * wrapper of {@link Query} that introduces a set of useful methods
 * for rules writing.
 * 
 * @author jgrande
 *
 */
public class AdvertQuery {
	
	private static Logger logger = LoggerFactory.getLogger(AdvertQuery.class);
	
	private ResponseBuilder rb;
	
	private Query q;
	
	public AdvertQuery(ResponseBuilder rb) {
		this.rb = rb;
		this.q = rb.getQuery();
	}
	
	/**
	 * @return <code>true</code> if this query contains a TermQuery
	 * for Term(field, text)
	 */
	public boolean hasTerm(String field, String text) {
		Term term = new Term(field, text);
		HasTermQueryVisitor queryVisitor = new HasTermQueryVisitor(term);
		return queryVisitor.visit(this.q);
	}
	
	/**
	 * Add the score returned by the query <code>qstr</code>
	 * to the original score of each document. By default,
	 * <code>qstr</code> is parsed with {@link FunctionQParserPlugin}, but
	 * this can be overridden using local params, eg:
	 * <code>{!lucene}field:text</code>.
	 * 
	 * @param qstr the query string to use 
	 */
	public void boost(String qstr) {
		logger.debug("Boosting with function query '" + qstr + "'");
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

}
