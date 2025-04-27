package com.example.rpms.model;

import java.util.ArrayList;

public class AppointmentManager {
    // static arraylist to hold all appointments
    private static ArrayList<Appointment> appointments = new ArrayList<>();

    // getter for appointments
    public static ArrayList<Appointment> getAppointments() {
        return appointments;
    }
    // viewing all appointments
    public static void viewAppointments(){
        System.out.println("Appointments:");
        for (Appointment a : appointments) {
            System.out.println(a.getDate() + " - " + a.getStatus() + " - " + a.getDoctor().getName() + " - " + a.getPatient().getName());
        }
    }
    // viewing pending appointments
    public static void viewPendingAppointments() {
        System.out.println("Pending Appointments:");
        for (Appointment a : appointments) {
            if (a.getStatus().equals("Pending")) {
                System.out.println(a.getDate() + " - " + a.getDoctor().getName() + " - " + a.getPatient().getName());
            }
        }
    }

    // viewing approved appointments
    public static void viewApprovedAppointments() {
        System.out.println("Approved Appointments:");
        for (Appointment a : appointments) {
            if (a.getStatus().equals("Approved")) {
                System.out.println(a.getDate() + " - " + a.getDoctor().getName() + " - " + a.getPatient().getName());
            }
        }
    }

    // viewing cancelled appointments


    // requesting a new appointment
    public static void requestAppointment(Appointment appointment) {
        appointments.add(appointment);
        System.out.println("Appointment added to queue. Waiting to be approved.");
    }

    // approving an appointment
    public static void approveAppointment(Appointment appointment) {
        appointment.setStatus("Approved");
        System.out.println("Appointment approved: " + appointment.getDate());
    }

    // cancelling an appointment
    public static void cancelAppointment(Appointment appointment) {
        appointment.setStatus("Cancelled");
        System.out.println("Appointment cancelled: " + appointment.getDate());
    }

}

