package com.mikejones.maestro;

public class User {


    private String email;
    private String username;
    private String role;

    public User(String em, String name, String usertype){
        email = em;
        username = name;
        role = usertype;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
