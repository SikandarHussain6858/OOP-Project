package com.example.rpms.model;

public class Prescription {
    // required attributes for prescription
    private String medication;
    private String dosage;
    private String schedule;

    // constructor
    public Prescription(String medication, String dosage, String schedule) {
        this.medication = medication;
        this.dosage = dosage;
        this.schedule = schedule;
    }
    // getters
    public String getMedication() { return medication; }
    public String getDosage() { return dosage; }
    public String getSchedule() { return schedule; }

    // setters
    public void setMedication(String medication) { this.medication = medication; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    // overriden toString for displaying a prescription
    @Override
    public String toString() {
        return "Medication: " + medication + ", Dosage: " + dosage + ", Schedule: " + schedule;
    }
}

