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

import static org.junit.Assert.assertNull;

import java.util.Date;

import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.util.TestHarness;
import org.apache.solr.util.TestHarness.LocalRequestFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractAdvertTestCase {
  
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();
  
  private TestHarness harness;
  
  private LocalRequestFactory requestFactory;
  
  @Before
  public void before() {
    harness = new TestHarness(getDataDirectory(), getSolrConfigFile(), getSchemaFile());
    requestFactory = harness.getRequestFactory("standard", 0, 20);
  }
  
  @After
  public void after() {
    harness.close();
  }
  
  public abstract String getSchemaFile();
  
  public abstract String getSolrConfigFile();
  
  public String getDataDirectory() {
    return tmpFolder.newFolder("data").getAbsolutePath();
  }
  
  protected void assertAddDoc(String id, String product, String brand, String description, Date date, Double price) throws Exception {
    assertNull(
        "Error adding document",
        harness.validateAddDoc(
            "id", id, 
            "product", product, 
            "brand", brand, 
            "description", description, 
            "price", price.toString())
        );
  }
  
  protected void assertCommit() throws Exception {
    assertNull("Error comitting", harness.validateUpdate("<commit/>"));
  }
  
  protected SolrQueryRequest newRequest(String... args) {
    return requestFactory.makeRequest(args);
  }
  
  protected void assertQuery(SolrQueryRequest req, String... tests) throws Exception {
    assertNull(harness.validateQuery(req, tests));
  }

}
