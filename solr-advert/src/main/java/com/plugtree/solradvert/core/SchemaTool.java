package com.plugtree.solradvert.core;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.schema.IndexSchema;

public class SchemaTool {
  
  private ResponseBuilder rb;
  
  public SchemaTool(ResponseBuilder rb) {
    this.rb = rb;
  }
  
  public String analyze(String field, String text) {
    IndexSchema schema = rb.req.getSchema();
    Analyzer analyzer = schema.getAnalyzer();
    StringReader reader = new StringReader(text);
    TokenStream ts = analyzer.tokenStream(field, reader);
    ts.addAttribute(CharTermAttribute.class);
    String result = null;
    
    try {
      if(ts.incrementToken()) {
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
        result = term.toString();
      }
      ts.close();
      reader.close();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
    
    return result;
  }

}
