package com.plugtree.solradvert.testmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BrandsLoader {
  
  public static List<Brand> load(InputStream in) throws IOException {
    List<Brand> brands = new ArrayList<Brand>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String line;
    
    while((line=reader.readLine())!=null) {
      Brand brand = new Brand();
      brand.setName(line);
      brands.add(brand);
    }
    
    reader.close();
    
    return brands;
  }

}
