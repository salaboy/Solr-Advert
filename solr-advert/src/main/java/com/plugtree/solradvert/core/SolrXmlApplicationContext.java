package com.plugtree.solradvert.core;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.solr.common.SolrException;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrResourceLoader;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

// TODO write tests!!!
public class SolrXmlApplicationContext extends AbstractXmlApplicationContext {
  
  private SolrResourceLoader loader;
  
  private File configDir;
  
  public SolrXmlApplicationContext(SolrCore core, String configLocation) {
    loader = core.getResourceLoader();
    configDir = new File(loader.getConfigDir());
    if(!configDir.exists()) {
      URL configUrl = loader.getClassLoader().getResource(loader.getConfigDir());
      if(configUrl==null) {
        throw new SolrException(SolrException.ErrorCode.SERVER_ERROR, 
            "Unable to find config dir for core '" + core.getName() + "'");
      }
      try {
        configDir = new File(configUrl.toURI());
      } catch (URISyntaxException e) {
        throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
            "Error while locating config dir for core '" + core.getName() + "'", e);
      }
    }
    setConfigLocation(configLocation);
  }
  
  @Override
  protected Resource getResourceByPath(String path) {
    File f = new File(path);
    
    if(f.isAbsolute()) {
      // if the path is absolute, check if it exists
      if(f.exists()) {
        return new FileSystemResource(f);
      }
    } else {
      // if the path is relative, check inside the config dir
      f = new File(configDir, path);
      if(f.exists()) {
        return new FileSystemResource(f);
      }
    }
    
    // if we didn't find the resource, check in the classpath
    URL resUrl = loader.getClassLoader().getResource(path);
    if(resUrl!=null) {
      return new UrlResource(resUrl);
    }
    
    throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
        "Unable to locate resource: " + path);
  }
  
  @Override
  public ClassLoader getClassLoader() {
    return loader.getClassLoader();
  }

}
