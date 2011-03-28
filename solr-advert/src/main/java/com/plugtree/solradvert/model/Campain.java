package com.plugtree.solradvert.model;

/**
 *  Copyright 2011 Plugtree LLC
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class Campain {
    private String name;
    private List<Product> boostProducts;
    private Date startDate;
    private Date endDate;

    public Campain(String name) {
        this.name = name;
    }

    public List<Product> getBoostProducts() {
        return boostProducts;
    }

    public void setBoostProducts(List<Product> boostProducts) {
        this.boostProducts = boostProducts;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public void addProduct(Product product){
        if(this.boostProducts == null){
            this.boostProducts = new ArrayList<Product>();
        }
        this.boostProducts.add(product);
    }
    
    
    
}
