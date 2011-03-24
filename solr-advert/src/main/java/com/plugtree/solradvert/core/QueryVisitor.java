package com.plugtree.solradvert.core;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QueryVisitor<T> {
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	public final T visit(Query q) {
		if(q instanceof TermQuery) {
			return visit((TermQuery)q);
		} else if(q instanceof BooleanQuery) {
			return visit((BooleanQuery)q);
		} else if(q instanceof DisjunctionMaxQuery) {
			return visit((DisjunctionMaxQuery)q);
		} else {
			log.warn("Found unknown query type: " + q.getClass().getName() + ". Stopping visit.");
			return null;
		}
	}
	
	public abstract T visit(TermQuery q);
	
	public abstract T visit(BooleanQuery q);
	
	public abstract T visit(DisjunctionMaxQuery q);

}
