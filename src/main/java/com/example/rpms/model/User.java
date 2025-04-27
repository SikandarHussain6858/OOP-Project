package com.example.rpms.model;

public class User {
    // basic data fields that all users will possess
    private final String id;
    private final String name;
    private String email;


    // constructor
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    // getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // setters
    // no setter for id and name because it should not be changed once created
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Email: " + email;
    }
}