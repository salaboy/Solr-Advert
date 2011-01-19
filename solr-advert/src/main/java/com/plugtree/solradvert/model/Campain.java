/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.plugtree.solradvert.model;

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
