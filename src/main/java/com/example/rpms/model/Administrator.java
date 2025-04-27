package com.example.rpms.model;

import java.util.ArrayList;

public class Administrator extends User {
    // lists of patients and doctors in the system
    // they are statics because we want to have a single list of doctors and patients for the whole system
    // so that we can access them from anywhere in the system
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    private static ArrayList<Patient> patients = new ArrayList<>();
    // list of system logs
    private static ArrayList<String> systemLogs;

    // constructor
    public Administrator(String id, String name, String email) {
        super(id, name, email);
    }

    // getters
    public static ArrayList<Doctor> getDoctors() { return doctors; }
    public static ArrayList<Patient> getPatients() { return patients; }
    public static ArrayList<String> getSystemLogs() { return systemLogs; }

    // adding new log
    public void addSystemLog(String log) {
        systemLogs.add(log);
    }
    // viewing all logs
    public void viewSystemLogs() {
        System.out.println("System Logs:");
        for (String log : systemLogs) {
            System.out.println(log);
        }
    }
    // registering new dovctor
    public void registerDoctor(Doctor doctor) {
        doctors.add(doctor);
        System.out.println("Doctor " + doctor.getName() + " registered.");
    }

    // registering new patient
    public void registerPatient(Patient patient) {
        patients.add(patient);
        System.out.println("Patient " + patient.getName() + " registered.");
    }

    //remoce a doctor from the system
    public void removeDoctor(Doctor doctor) {
        if (doctors.remove(doctor)) {
            System.out.println("Doctor " + doctor.getName() + " removed from the system.");
        } else {
            System.out.println("Doctor not found.");
        }
    }
    //remove a patient from the system
    public void removePatient(Patient patient) {
        if (patients.remove(patient)) {
            System.out.println("Patient " + patient.getName() + " removed from the system.");
        } else {
            System.out.println("Patient not found.");
        }
    }
}