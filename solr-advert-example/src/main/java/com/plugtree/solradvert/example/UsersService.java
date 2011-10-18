package com.plugtree.solradvert.example;

import java.util.HashMap;
import java.util.Map;

public class UsersService {
  
  private final Map<String, User> users;
  
  public UsersService() {
    users = new HashMap<String, User>();
    users.put("john", new User("john", 27));
    users.put("peter", new User("peter", 52));
    users.put("mary", new User("mary", 41));
  }
  
  public User getUser(String userid) {
    return users.get(userid);
  }

}
