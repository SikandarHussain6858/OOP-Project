package com.example.rpms.model;

import java.time.LocalDateTime;

public class Appointment {
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime appointmentDateTime;
    private boolean confirmed;

    public Appointment(Patient patient, Doctor doctor, LocalDateTime appointmentDateTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.confirmed = false;
    }

    // Getters and Setters
}
