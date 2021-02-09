package com.allansantosh.mytrack;

public class User {

    private String id;
    private String username;
    private String name;
    private String email;


    public User(String id, String username, String name, String email) {

        this.id = id;
        this.username = username;
        this.name= name;
        this.email = email;

    }

    public String getId() { return id; }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}