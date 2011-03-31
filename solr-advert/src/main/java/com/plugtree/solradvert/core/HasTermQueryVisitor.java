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

import java.util.Iterator;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class HasTermQueryVisitor extends QueryVisitor<Boolean> {
	
	private Term term;
	
	public HasTermQueryVisitor(Term term) {
		this.term = term;
	}
	
	@Override
	protected Boolean getDefaultValue() {
	  return Boolean.FALSE;
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
		Iterator<Query> it = q.iterator();
		while(it.hasNext()) {
			Query qq = it.next();
			if(Boolean.TRUE.equals(visit(qq))) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	@Override
	public Boolean visit(MatchAllDocsQuery q) {
	  return Boolean.FALSE;
	}

}
