package com.example.rpms.model;

import java.util.List;

public class Doctor extends User {
    private List<Patient> assignedPatients;

    public Doctor(String username, String password, String name) {
        super(username, password, name);
    }
    public String getName() {
        return name;
    }
    // Methods to provide feedback, view patient data
}
