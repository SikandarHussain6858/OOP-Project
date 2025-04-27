package com.example.rpms.model;

import java.util.List;

public class Patient extends User {
    private List<Vitals> vitalsHistory;

    public Patient(String username, String password, String name) {
        super(username, password, name);
    }
    public String getName() {
        return name;
    }
    // Methods to add vitals, view history
}
