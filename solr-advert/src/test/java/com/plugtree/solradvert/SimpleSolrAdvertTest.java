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
 *			<str name="qf">id product brand description</str>
 *		</lst>
 *		<arr name="first-components">
 *			<str>adverts</str>
 *		</arr>
 *	</requestHandler>
 *
 *	<requestHandler name="requestHandlerWithoutAdvert" class="solr.StandardRequestHandler">
 *		<lst name="defaults">
 *			<str name="defType">dismax</str>
 *			<str name="qf">id product brand description</str>
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


	private void addDoc(String id, String product, String brand,
			String description, Date date) {
		assertU(adoc("id", id, "product", product, "brand", brand, "description", description));
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
		this.addDoc("1", "shoes", "nike", "running shoes", new Date());
		this.addDoc("2", "shoes", "adidas", "football shoes", new Date());
		assertU(commit());
		
		// we are using the requestHandlerWithAdvert, but the request parameter
		// "advert" isn't "true", so the documents should be returned in their
		// index order
		assertQ(req("q","shoes", "qt", "requestHandlerWithAdvert"),
				"//*[@numFound='2']",
				"//result/doc[1]/int[@name='id'][.='1']",
				"//result/doc[2]/int[@name='id'][.='2']"
		);
		
		// now we add "&advert=true" to the request, so the Adidas shoes should
		// be boosted (see advert.drl)
		assertQ(req("q","shoes", "qt", "requestHandlerWithAdvert", "advert", "true"),
				"//*[@numFound='2']",
				"//result/doc[1]/int[@name='id'][.='2']",
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
