package com.kolmanfreecss.domain.model;

public class User {
    
    long id;
    String name;
    
    public User() {
    }
    
    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
}
