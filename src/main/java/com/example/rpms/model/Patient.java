package com.example.rpms.model;

import java.util.ArrayList;

public class Patient extends User {
    // attributes specific to the Patient class
    private VitalsDatabase vitalsDatabase;  // to store the vital signs of the patient
    private ArrayList<Feedback> feedbacks;  // to store previous feedbacks given by doctors

    // constructor to initialize the Patient object
    public Patient(String id, String name, String email) {
        super(id, name, email);
        // new vitals database objject for each patient4
        this.vitalsDatabase = new VitalsDatabase();
        // new arraylist for feedbacks for each patient
        this.feedbacks = new ArrayList<>();
    }

    // getter for vitalsdatabase of each patient
    public VitalsDatabase getVitals() {
        return vitalsDatabase;
    }

    // getter for feedback
    public ArrayList<Feedback> getFeedbacks() {
        return feedbacks;
    }

    // no setters because doesnt make sense to change vitasldatabase and feedbacks after the patient has been created


    // for uploading a new vital sign
    public void uploadVitalSign(VitalSign vital) {
        vitalsDatabase.addVital(vital);
        System.out.println("Vital sign added for patient: " + getName());
    }

    // requesting a new appointment
    public void requestAppointment(Appointment appointment) {
        AppointmentManager.requestAppointment(appointment);
        System.out.println("Appointment requested for: " + getName());
    }
    // adding a new feedback
    public void addFeedback(Feedback feedback) {
        feedbacks.add(feedback);
        System.out.println("Feedback added for patient: " + getName());
    }
    // removing a feedback
    public void removeFeedback(Feedback feedback) {
        feedbacks.remove(feedback);
        System.out.println("Feedback removed for patient: " + getName());
    }

    // removing a vital sign
    public void removeVital(VitalSign vitalSign){
        vitalsDatabase.removeVital(vitalSign);
    }

    // viewing previous feedbacks
    public void viewPreviousFeedbacks() {
        System.out.println("Feedbacks for " + getName() + ":");
        for (Feedback f : feedbacks) {
            System.out.println(f.getComments());
        }
    }
    // viewing previous feedbacks
    public void viewPreviousVitals(){
        System.out.println("Vital Signs for " + getName() + ":");
        vitalsDatabase.displayVitals();
    }


    // no toString bcs user's can be used. no need to didplay vitals and feedbacks in this
}
