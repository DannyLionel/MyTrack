package com.allansantosh.mytrack;

public class AuthorizedUser {

    private String id;
    private String name;


    public AuthorizedUser(String id, String name) {

        this.id = id;
        this.name= name;

    }

    public String getId() { return id; }

    public String getName() {
        return name;
    }

}
