/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import java.io.IOException;
import java.util.Date;

import org.apache.solr.util.AbstractSolrTestCase;

/**
 * Using the request hanlders:
 * 
 * <requestHandler name="requestHandlerWithAdvert" class="solr.StandardRequestHandler">
 *		<lst name="defaults">
 *			<str name="defType">dismax</str>
 *			<str name="qf">id content author brand </str>
 *		</lst>
 *		<arr name="first-components">
 *			<str>adverts</str>
 *		</arr>
 *	</requestHandler>
 *
 *	<requestHandler name="requestHandlerWithoutAdvert" class="solr.StandardRequestHandler">
 *		<lst name="defaults">
 *			<str name="defType">dismax</str>
 *			<str name="qf">id content author brand </str>
 *		</lst>
 *	</requestHandler>
 *
 * @author salaboy
 */
public class SimpleSolrAdvertTest extends AbstractSolrTestCase {




	@Override
	public void setUp() throws Exception {
		super.setUp();
	}


	private void addDoc(String id, String user, String content,
			String brand, Date date) {
		assertU(adoc("id", id, "author", user, "content", content, "brand", brand));
	}


	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	public String getSchemaFile() {
		return "schema.xml";
	}

	@Override
	public String getSolrConfigFile() {
		return "solrconfig.xml";
	}

	
	public void testSimplestCase() throws IOException, Exception {
		this.addDoc("1", "user1", "something", "some brand", new Date());
		this.addDoc("2", "user2", "something", "some brand", new Date());
		assertU(commit());
		
		assertQ(req("q","something", "qt", "requestHandlerWithoutAdvert"), // qt defines the request handler to use. The query should match both documents
				"//*[@numFound='2']",
				"//result/doc[1]/int[@name='id'][.='1']", //number 1 will be the first document, with the same score
				"//result/doc[2]/int[@name='id'][.='2']"
		);
		
		assertQ(req("q","something", "qt", "requestHandlerWithAdvert"), // using AdvertComponent
				"//*[@numFound='2']",
				"//result/doc[1]/int[@name='id'][.='2']", //number 2 should have been boosted
				"//result/doc[2]/int[@name='id'][.='1']"
		);

	}

	public void testQueryGeneralQuery() {
		assertQ(req("q","some content"));
	}

//	Will use a different request handler for these queries	
//	public void testFieldedQuery() {
//		assertQ(req("q","content:(some content2)"));
//	}
//
//	public void testFieldedQuery2() {
//		assertQ(req("q","brand:(some brand)"));
//	}

}
