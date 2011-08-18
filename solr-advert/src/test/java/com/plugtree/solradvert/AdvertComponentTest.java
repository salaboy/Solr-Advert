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

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.solr.common.SolrException;
import org.junit.Test;

import com.plugtree.solradvert.util.AbstractAdvertTestCase;
import com.plugtree.solradvert.util.SolrTest;

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
public class AdvertComponentTest extends AbstractAdvertTestCase {
  
  @Test
  @SolrTest
	public void testBoosting() throws Exception {
		assertAddDoc("1", "shoes", "nike", "running shoes", new Date(), 0.0);
		assertAddDoc("2", "shoes", "adidas", "football shoes", new Date(), 0.0);
		assertCommit();
		
		// we are using the requestHandlerWithAdvert, but the request parameter
		// "advert" isn't "true", so the documents should be returned in their
		// index order
		assertQuery(
		    newRequest(
		        "q","shoes", 
		        "qt", "requestHandlerWithAdvert"),
				"//*[@numFound='2']",
				"//result/doc[1]/int[@name='id'][.='1']",
				"//result/doc[2]/int[@name='id'][.='2']"
		);
		
		// now we add "&advert=true" to the request, so the Adidas shoes should
		// be boosted (see rules/advert1.drl)
		assertQuery(
		    newRequest(
		        "q","shoes", 
		        "qt", "requestHandlerWithAdvert", 
		        AdvertParams.ADVERT_COMPONENT_NAME, "true"),
				"//*[@numFound='2']",
				"//result/doc[1]/int[@name='id'][.='2']",
				"//result/doc[2]/int[@name='id'][.='1']"
		);
	}
  
  @Test
  @SolrTest
  public void testBoostingDsl() throws Exception {
    assertAddDoc("1", "shoes", "nike", "running shoes", new Date(), 0.0);
    assertAddDoc("2", "shoes", "adidas", "football shoes", new Date(), 0.0);
    assertCommit();
    
    // we are using the requestHandlerWithAdvert, but the request parameter
    // "advert" isn't "true", so the documents should be returned in their
    // index order
    assertQuery(
        newRequest(
            "q","shoes", 
            "qt", "requestHandlerWithAdvert"),
        "//*[@numFound='2']",
        "//result/doc[1]/int[@name='id'][.='1']",
        "//result/doc[2]/int[@name='id'][.='2']"
    );
    
    // now we add "&advert=true" to the request, so the Adidas shoes should
    // be boosted
    assertQuery(
        newRequest(
            "q","shoes", 
            "qt", "requestHandlerWithAdvert", 
            AdvertParams.ADVERT_COMPONENT_NAME, "true",
            AdvertParams.ADVERT_RULES, "ksession4"),
        "//*[@numFound='2']",
        "//result/doc[1]/int[@name='id'][.='2']",
        "//result/doc[2]/int[@name='id'][.='1']"
    );

  }
	
  @Test
  @SolrTest
	public void testSorting() throws Exception {
    assertAddDoc("1", "tennis racquet", "babolat", "", new Date(), 150.0);
    assertAddDoc("2", "tennis racquet", "prince", "", new Date(), 100.0);
    assertAddDoc("3", "tennis racquet", "head", "", new Date(), 300.0);
    assertCommit();
    
    // we are using the requestHandlerWithAdvert, but the request parameter
    // "advert" isn't "true", so the documents should be returned in their
    // index order
    assertQuery(
        newRequest(
            "q","\"tennis racquet\"", 
            "qt", "requestHandlerWithAdvert"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='1']",
        "//result/doc[2]/int[@name='id'][.='2']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );
    
    // now we add "&advert=true" to the request, so the documents should be
    // returned sorted by price
    assertQuery(
        newRequest(
            "q","\"tennis racquet\"", 
            "qt", "requestHandlerWithAdvert",
            AdvertParams.ADVERT_COMPONENT_NAME, "true"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='2']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );

  }
	
  @Test
  @SolrTest
	public void testTwoDifferentSessions() throws Exception {
    assertAddDoc("1", "tennis racquet", "babolat", "", new Date(), 150.0);
    assertAddDoc("2", "tennis racquet", "prince", "", new Date(), 100.0);
    assertAddDoc("3", "tennis racquet", "head", "", new Date(), 300.0);
    assertCommit();
    
    // No rules applied
    assertQuery(
        newRequest(
            "q","\"tennis racquet\"", 
            "qt", "requestHandlerWithAdvert"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='1']",
        "//result/doc[2]/int[@name='id'][.='2']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );
    
    // ksession1 will sort results by price in ascending order
    assertQuery(
        newRequest(
            "q","\"tennis racquet\"", 
            "qt", "requestHandlerWithAdvert",
            AdvertParams.ADVERT_COMPONENT_NAME, "true",
            AdvertParams.ADVERT_RULES, "ksession1"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='2']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );
    
    // ksession3 will sort results by price in descending order
    assertQuery(
        newRequest(
            "q","\"tennis racquet\"", 
            "qt", "requestHandlerWithAdvert",
            AdvertParams.ADVERT_COMPONENT_NAME, "true", 
            AdvertParams.ADVERT_RULES, "ksession3"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='3']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='2']"
    );
	}
	
  @Test(expected=SolrException.class)
  @SolrTest
	public void testUndefinedSession() throws Exception {
    assertQuery(
	      newRequest(
	          "q","foo", 
	          "qt", "requestHandlerWithAdvert",
	          AdvertParams.ADVERT_COMPONENT_NAME, "true",
	          AdvertParams.ADVERT_RULES, "bar123"),
        "//*[@numFound='0']"
    );
	}
  
  @Test
  @SolrTest(solrConfig="solrconfig-context3.xml")
	public void testRulesChangeBetweenRequests() throws Exception {
    assertAddDoc("1", "", "", "", new Date(), 150.0);
    assertAddDoc("2", "", "", "", new Date(), 100.0);
    assertAddDoc("3", "", "", "", new Date(), 300.0);
    assertCommit();
    
    File advert3File = new File(getClass().getResource("/rules/testSortByPriceDesc.drl").toURI());
    File advert5File = new File(getClass().getResource("/rules/testSortByPriceAsc.drl").toURI());
    File tempFile = tmpFolder.newFile("temp.drl");

    // advert3.drl will sort results by price in descending order
    assertQuery(
        newRequest(
            "q", "*:*", 
            AdvertParams.ADVERT_COMPONENT_NAME, "true"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='3']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='2']"
    );
    
    FileUtils.copyFile(advert3File, tempFile);
    
    try {
      FileUtils.copyFile(advert5File, advert3File);
      
      // advert5.drl will sort results by price in ascending order
      assertQuery(
          newRequest(
              "q", "*:*", 
              AdvertParams.ADVERT_COMPONENT_NAME, "true",
              AdvertParams.ADVERT_RELOAD_RULES, "true"),
          "//*[@numFound='3']",
          "//result/doc[1]/int[@name='id'][.='2']",
          "//result/doc[2]/int[@name='id'][.='1']",
          "//result/doc[3]/int[@name='id'][.='3']"
      );
    } finally {
      FileUtils.copyFile(tempFile, advert3File);
    }
	}
  
  @Test
  @SolrTest
  public void testMatchAllDocs() throws Exception {
    assertQuery(
        newRequest(
            "q", "{!lucene}*:*",
            "qt", "requestHandlerWithAdvert",
            AdvertParams.ADVERT_COMPONENT_NAME, "true"));
  }
  
  @Test
  @SolrTest
  public void testPhraseQuery() throws Exception {
    assertQuery(
        newRequest(
            "q", "{!lucene}description:i-pod",
            "qt", "requestHandlerWithAdvert",
            AdvertParams.ADVERT_COMPONENT_NAME, "true"));
  }
  
  @Test
  @SolrTest(solrConfig="solr/conf/solrconfig2.xml")
  public void testAnotherKnowledgeContext() throws IOException, Exception {
    assertAddDoc("1", "tennis racquet", "babolat", "", new Date(), 150.0);
    assertAddDoc("2", "tennis racquet", "prince", "", new Date(), 100.0);
    assertAddDoc("3", "tennis racquet", "head", "", new Date(), 300.0);
    assertCommit();
    
    // now we add "&advert=true" to the request, so the documents should be
    // returned sorted by price
    assertQuery(
        newRequest(
            "q", "\"tennis racquet\"", 
            "qt", "requestHandlerWithAdvert", 
            AdvertParams.ADVERT_COMPONENT_NAME, "true",
            AdvertParams.ADVERT_RULES, "sessionFromAnotherContext"
        ),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='3']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='2']"
    );

  }

  @Test
  @SolrTest
	public void testQueryGeneralQuery() throws Exception {
    assertQuery(
		    newRequest("q", "some content"));
	}
  
  @Test
  @SolrTest
  public void testMoveTermToFilter() throws Exception {
    assertAddDoc("1", "shoes", "adidas", "", new Date(), 150.0);
    assertAddDoc("2", "shoes", "nike", "", new Date(), 100.0);
    assertAddDoc("3", "socks", "adidas", "", new Date(), 300.0);
    assertCommit();
    
    // assert that nothing happens when the rule is disabled
    assertQuery(
        newRequest(
            "q", "adidas shoes", 
            "qt", "requestHandlerWithAdvert",
            "mm", "1",
            AdvertParams.ADVERT_COMPONENT_NAME, "false",
            AdvertParams.ADVERT_BATCH, "batchTestMoveTermToFilter"
        ),
        "//*[@numFound='3']"
    );
    
    // assert that the rule is not applied when the term appears in a filter query
    assertQuery(
        newRequest(
            "q", "shoes", 
            "qt", "requestHandlerWithAdvert",
            "fq", "brand:adidas OR brand:nike",
            "mm", "1",
            AdvertParams.ADVERT_COMPONENT_NAME, "true",
            AdvertParams.ADVERT_BATCH, "batchTestMoveTermToFilter",
            AdvertParams.ADVERT_RULES, "ksessionTestMoveTermToFilter"
        ),
        "//*[@numFound='2']"
    );
    
    // assert that the rule is applied when the term appears in the main query
    assertQuery(
        newRequest(
            "q", "adidas shoes", 
            "qt", "requestHandlerWithAdvert",
            "mm", "1",
            AdvertParams.ADVERT_COMPONENT_NAME, "true",
            AdvertParams.ADVERT_BATCH, "batchTestMoveTermToFilter",
            AdvertParams.ADVERT_RULES, "ksessionTestMoveTermToFilter"
        ),
        "//*[@numFound='1']"
    );
  }
	
}
