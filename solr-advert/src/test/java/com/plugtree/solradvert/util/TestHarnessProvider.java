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

import java.io.File;
import java.io.IOException;

import org.apache.solr.util.TestHarness;
import org.apache.solr.util.TestHarness.LocalRequestFactory;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class TestHarnessProvider implements MethodRule {
  
  private TestHarness harness;
  
  private LocalRequestFactory requestFactory;
  
  @Override
  public Statement apply(Statement base, FrameworkMethod method, Object obj) {
    SolrTest solrTestAnn = method.getAnnotation(SolrTest.class);
    if(solrTestAnn!=null) {
      return new SolrStatement(base, solrTestAnn.solrConfig(), solrTestAnn.schema());
    }
    return base;
  }
  
  public TestHarness getHarness() {
    return harness;
  }
  
  public LocalRequestFactory getRequestFactory() {
    return requestFactory;
  }
  
  private class SolrStatement extends Statement {
    
    private String solrConfig;
    
    private String schema;
    
    private Statement next;
    
    public SolrStatement(Statement next, String solrConfig, String schema) {
      this.solrConfig = solrConfig;
      this.schema = schema;
      this.next = next;
    }
    
    @Override
    public void evaluate() throws Throwable {
      openHarness();
      try {
        next.evaluate();
      } finally {
        closeHarness();
      }
    }
    
    private void openHarness() throws IOException {
      harness = new TestHarness(getDataDir(), solrConfig, schema);
      requestFactory = harness.getRequestFactory("standard", 0, 20);
    }
    
    private void closeHarness() {
      if(harness!=null) {
        harness.close();
      }
    }
    
    private String getDataDir() throws IOException {
      File dataDir = File.createTempFile("solr-test", "data");
      if(dataDir.exists()) {
        dataDir.delete();
      }
      dataDir.mkdir();
      return dataDir.getAbsolutePath();
    }
    
  }

}
