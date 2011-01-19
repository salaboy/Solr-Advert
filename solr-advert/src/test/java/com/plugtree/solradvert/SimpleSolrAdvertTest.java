/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert;

import java.util.Date;

import org.apache.solr.util.AbstractSolrTestCase;

/**
 *
 * @author salaboy
 */
public class SimpleSolrAdvertTest extends AbstractSolrTestCase {
    
   

    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.addField("1", "user1", "some content", "some brand", new Date());
        this.addField("2", "user2", "some content2", "some brand", new Date());
        this.addField("3", "user1", "some content3", "some brand2", new Date());
        assertU(commit());
    }

    
    private void addField(String id, String user, String content,
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

    
    public void testHello() {
    	System.out.println("hello");
    }
    
    public void testSimpleCall() {
    	assertQ("test Simple call", req("q","*:*"));
    }

}
