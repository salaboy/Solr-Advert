package com.plugtree.solradvert.example;

public class User {
  
  private String userid;
  
  private int age;
  
  public User(String userid, int age) {
    this.userid = userid;
    this.age = age;
  }
  
  public int getAge() {
    return age;
  }
  
  public String getUserid() {
    return userid;
  }

}
