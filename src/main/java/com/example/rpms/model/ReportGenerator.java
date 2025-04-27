package com.example.rpms.model;

public class ReportGenerator {
    public void generatePatientReport(Patient patient) {
        System.out.println("Generating report for: " + patient.getName());
        // Later: Export as PDF or text file
    }
}
