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

public class AdvertConfigurationTest extends AbstractAdvertTestCase {

  @Override
  public String getSchemaFile() {
    return "schema.xml";
  }

  @Override
  public String getSolrConfigFile() {
    return "solrconfig2.xml";
  }
  
  public void testKContextParameter() throws IOException, Exception {
    this.addDoc("1", "tennis racquet", "babolat", "", new Date(), 150.0);
    this.addDoc("2", "tennis racquet", "prince", "", new Date(), 100.0);
    this.addDoc("3", "tennis racquet", "head", "", new Date(), 300.0);
    assertU(commit());
    
    // now we add "&advert=true" to the request, so the documents should be
    // returned sorted by price (see advert.drl)
    assertQ(req("q","\"tennis racquet\"", 
        "qt", "requestHandlerWithAdvert", 
        AdvertParams.ADVERT_COMPONENT_NAME, "true"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='2']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );

  }

}
