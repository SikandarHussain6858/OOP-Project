package com.example.rpms.model;

import java.util.ArrayList;
import java.util.Date;


public class Feedback {
    // datafields
    private String comments;
    private ArrayList<Prescription> prescriptions; // ArrayList to hold prescriptions
    private Date date;// date of the feedback

    public Feedback(String comments, ArrayList<Prescription> prescriptions, Date date) {
        this.date = date;
        this.comments = comments;
        // initializing the arraylist of prescriptions
        this.prescriptions = prescriptions;
    }

    // getters and setters
    public String getComments() { return comments; }
    public Date getDate() { return date; }
    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }

    // setters
    public void setComments(String comments) { this.comments = comments; }
    public void setDate(Date date) { this.date = date; }

    // adding new prescriptions
    public void addPrescriptions(Prescription prescription) {
        this.prescriptions.add(prescription);
    }

    // overriden tostring to display details
    @Override
    public String toString() {
        return "Comments: " + comments +
                "\nPrescriptions: " + prescriptions;
    }
}

