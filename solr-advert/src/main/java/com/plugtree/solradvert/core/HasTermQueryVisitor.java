package com.plugtree.solradvert.core;

import java.util.Iterator;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class HasTermQueryVisitor extends QueryVisitor<Boolean> {
	
	private Term term;
	
	public HasTermQueryVisitor(Term term) {
		this.term = term;
	}
	
	@Override
	public Boolean visit(BooleanQuery q) {
		log.debug("Visiting BooleanQuery");
		for(BooleanClause clause: q.getClauses()) {
			if(Boolean.TRUE.equals(visit(clause.getQuery()))) {
				return Boolean.TRUE;
			}
		}
		log.debug("The query doesn't contain the term " + term.toString());
		return Boolean.FALSE;
	}
	
	@Override
	public Boolean visit(TermQuery q) {
		log.debug("Visiting TermQuery for term " + q.getTerm().toString());
		boolean result = q.getTerm().compareTo(term)==0;
		if(result) {
		  log.debug("The query contains the term " + term.toString());
		}
		return result;
	}
	
	@Override
	public Boolean visit(DisjunctionMaxQuery q) {
		log.debug("Visiting DisjunctionMaxQuery");
		@SuppressWarnings("unchecked") Iterator<Query> it = q.iterator();
		while(it.hasNext()) {
			Query qq = it.next();
			if(Boolean.TRUE.equals(visit(qq))) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

}
