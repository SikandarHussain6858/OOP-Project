package com.example.rpms.model;

import java.util.ArrayList;

public class VitalsDatabase {
    // Static list to hold all vital signs
    private ArrayList<VitalSign> vitals = new ArrayList<>();
    // getter for vitals
    public ArrayList<VitalSign> getVitals(){
        return vitals;
    }

    // adding a new vital
    public void addVital(VitalSign vital) {
        vitals.add(vital);
        System.out.println("Vital sign added to database.");
    }
    // removing a vital sign
    public void removeVital(VitalSign vital) {
        vitals.remove(vital);
        System.out.println("Vital sign removed from database.");
    }
    // if all the vital signs are to be displayed for a specific user ID
    public void displayVitals() {
        System.out.println("Vital Signs: ");
        for (VitalSign v : vitals) {
            System.out.println(v);
        }
    }

}

