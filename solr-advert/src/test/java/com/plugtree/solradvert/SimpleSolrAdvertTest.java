package com.plugtree.solradvert;

/**
 *      Copyright 2011 Plugtree LLC
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

import java.io.IOException;
import java.util.Date;

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
public class SimpleSolrAdvertTest extends AbstractAdvertTestCase {

	@Override
	public String getSchemaFile() {
		return "schema.xml";
	}

	@Override
	public String getSolrConfigFile() {
		return "solrconfig.xml";
	}

	public void testBoosting() throws IOException, Exception {
		this.addDoc("1", "shoes", "nike", "running shoes", new Date(), 0.0);
		this.addDoc("2", "shoes", "adidas", "football shoes", new Date(), 0.0);
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
	
	public void testSorting() throws IOException, Exception {
    this.addDoc("1", "tennis racquet", "babolat", "", new Date(), 150.0);
    this.addDoc("2", "tennis racquet", "prince", "", new Date(), 100.0);
    this.addDoc("3", "tennis racquet", "head", "", new Date(), 300.0);
    assertU(commit());
    
    // we are using the requestHandlerWithAdvert, but the request parameter
    // "advert" isn't "true", so the documents should be returned in their
    // index order
    assertQ(req("q","\"tennis racquet\"", "qt", "requestHandlerWithAdvert"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='1']",
        "//result/doc[2]/int[@name='id'][.='2']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );
    
    // now we add "&advert=true" to the request, so the documents should be
    // returned sorted by price (see advert.drl)
    assertQ(req("q","\"tennis racquet\"", "qt", "requestHandlerWithAdvert", "advert", "true"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='2']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );

  }
	
	public void testAnotherSession() {
	  this.addDoc("1", "tennis racquet", "babolat", "", new Date(), 150.0);
    this.addDoc("2", "tennis racquet", "prince", "", new Date(), 100.0);
    this.addDoc("3", "tennis racquet", "head", "", new Date(), 300.0);
    assertU(commit());
    
    // ksession1 will sort results by price in descending order
    assertQ(req("q","\"tennis racquet\"", "qt", "requestHandlerWithAdvert", "advert", "true", "advert.rules", "ksession1"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='2']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );
    
    // ksession1 will sort results by price in ascending order
    assertQ(req("q","\"tennis racquet\"", "qt", "requestHandlerWithAdvert", "advert", "true", "advert.rules", "ksession2"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='3']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='2']"
    );
	}
	
	public void testUndefinedSession() {
	  assertQ(req("q","foo", "qt", "requestHandlerWithAdvert", "advert", "true", "advert.rules", "bar123"),
        "//*[@numFound='0']"
    );
	}

	public void testQueryGeneralQuery() {
		assertQ(req("q","some content"));
	}
	
}
