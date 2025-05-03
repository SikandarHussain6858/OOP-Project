package com.example.rpms.model;

import java.util.Date;

public class Appointment {
    // data fields
    private long date;
    private String status;
    private Doctor doctor;
    private Patient patient;

    // constructor
    public Appointment(String date, String doctorName, String patientName) {
        this.date = Date.parse(date);
        // using setters to ensure validation
        setDoctor(findDoctorByName(doctorName));
        setPatient(findPatientByName(patientName));
        setStatus("Pending");
    }

    private Doctor findDoctorByName(String name) {
        for (Doctor d : Administrator.getDoctors()) {
            if (d.getName().equals(name)) {
                return d;
            }
        }
        System.out.println("Doctor not found in the hospital system.");
        return null;
    }

    private Patient findPatientByName(String name) {
        for (Patient p : Administrator.getPatients()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        System.out.println("Patient not found in the hospital system.");
        return null;
    }

    // getters
    public long getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    // setters
    public void setDate(long date) {
        this.date = date;
    }

    public void setStatus(String status) {
        // validation for states of status
        if (status.equals("Pending") || status.equals("Approved") || status.equals("Cancelled")) {
            this.status = status;
        } else {
            System.out.println("Invalid status. Status must be either 'Pending', 'Approved', or 'Cancelled'.");
        }
    }

    public void setDoctor(Doctor doctor) {
        // checking if the doctor exists in the hospital
        for (Doctor d : Administrator.getDoctors()) {
            if (d.equals(doctor)) {
                this.doctor = doctor;
                return;
            }
        }
        System.out.println("Doctor not found in the hospital system.");
    }

    public void setPatient(Patient patient) {
        // checking if the patient exists in the hospital
        for (Patient p : Administrator.getPatients()) {
            if (p.equals(patient)) {
                this.patient = patient;
                return;
            }
        }
        System.out.println("Patient not found in the hospital system.");
    }
    public void viewAppointmentDetails() {
        System.out.println("Appointment Details:");
        System.out.println("Date: " + new Date(date));
        System.out.println("Status: " + status);
        System.out.println("Doctor: " + doctor.getName());
        System.out.println("Patient: " + patient.getName());
    }
    public void viewAppointments() {
        System.out.println("Appointments:");
        for (Appointment a : AppointmentManager.getAppointments()) {
            if (a.getDoctor().equals(this.doctor)) {
                System.out.println(a.getDate() + " - " + a.getStatus());
            }
        }
    }
}