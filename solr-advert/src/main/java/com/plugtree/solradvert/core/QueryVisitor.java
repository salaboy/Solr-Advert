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
