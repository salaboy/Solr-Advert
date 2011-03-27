package com.plugtree.solradvert;

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
    assertQ(req("q","\"tennis racquet\"", "qt", "requestHandlerWithAdvert", "advert", "true"),
        "//*[@numFound='3']",
        "//result/doc[1]/int[@name='id'][.='2']",
        "//result/doc[2]/int[@name='id'][.='1']",
        "//result/doc[3]/int[@name='id'][.='3']"
    );

  }

}
