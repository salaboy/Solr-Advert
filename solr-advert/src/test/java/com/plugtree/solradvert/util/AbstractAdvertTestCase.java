package com.plugtree.solradvert.util;

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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.apache.lucene.search.Query;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class AbstractAdvertTestCase {
  
  private Collection<Object> mocks = null;
  
  @Rule
  public TemporaryFolder tmpFolder = new TemporaryFolder();
  
  @Rule
  public TestHarnessProvider harnessProvider = new TestHarnessProvider();
  
  @After
  public void validateMocks() {
    if(mocks!=null) {
      EasyMock.verify(mocks.toArray());
      mocks = null;
    }
  }
  
  protected <T> T createMock(Class<T> clazz) {
    T mock = EasyMock.createMock(clazz);
    
    if(mocks==null) {
      mocks = new LinkedList<Object>();
    }
    
    mocks.add(mock);
    
    return mock;
  }
  
  protected void replayAllMocks() {
    if(mocks!=null) {
      EasyMock.replay(mocks.toArray());
    }
  }
  
  public String getDataDirectory() {
    return tmpFolder.newFolder("data").getAbsolutePath();
  }
  
  protected Query getQuery(String qstr) throws Exception {
    SolrQueryRequest req = newRequest("q", qstr);
    QParser qparser = QParser.getParser(req.getParams().get(CommonParams.Q), "lucene", req);
    return qparser.getQuery();
  }
  
  protected void assertAddDoc(String id, String product, String brand, String description, Date date, Double price) throws Exception {
    assertNull(
        "Error adding document",
        harnessProvider.getHarness().validateAddDoc(
            "id", id, 
            "product", product, 
            "brand", brand, 
            "description", description, 
            "price", price.toString())
        );
  }
  
  protected void assertCommit() throws Exception {
    assertNull("Error comitting", harnessProvider.getHarness().validateUpdate("<commit/>"));
  }
  
  protected SolrQueryRequest newRequest(String... args) {
    return harnessProvider.getRequestFactory().makeRequest(args);
  }
  
  protected void assertQuery(SolrQueryRequest req, String... tests) throws Exception {
    assertNull(harnessProvider.getHarness().validateQuery(req, tests));
  }

}
