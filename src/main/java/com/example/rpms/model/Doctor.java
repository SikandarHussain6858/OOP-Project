package com.example.rpms.model;
import java.util.ArrayList;
import java.util.List;

// required imports

public class Doctor extends User {
    // arraylists of patients that are assigned to a Doctor
    private ArrayList<Patient> patients;

    // constructor
    public Doctor(String id, String name, String email) {
        super(id, name, email);
        this.patients = new ArrayList<>();  // iniitializing the new arraylist of patients for each doctor
    }
    public Doctor() {
        super("defaultId", "defaultName", "defaultEmail"); // Provide default values
        this.patients = new ArrayList<>();
    }

    public static boolean removeDoctorByIdNameEmail(String id, String name, String email) {
        return true;
    }

    // gettern for patients
    public ArrayList<Patient> getPatients() {
        return patients;
    }

    // no setter for patients bcs doesnt make sense


    // adding a new patient
    public void addPatient(Patient patient) {
        patients.add(patient);
        System.out.println("Patient " + patient.getName() + " added to Dr. " + getName() + "'s list.");
    }

    // giving feedback to a patient
    public void provideFeedback(Patient patient, Feedback feedback) {
        patient.addFeedback(feedback);
    }

    // viewing appointments for doctors
    public void viewAppointments() {
        System.out.println("Appointments for Dr. " + getName() + ":");
        for (Appointment a : AppointmentManager.getAppointments()) {
            if (a.getDoctor().equals(this)) {
                System.out.println(a.getDate() + " - " + a.getStatus());
            }
        }
    }

    // viewing patients
    public void viewPatients() {
        System.out.println("Patients for Dr. " + getName() + ":");
        for (Patient p : patients) {
            System.out.println(p.getName());
        }
    }

    // viewing patient feedbacks
    public void viewPatientFeedbacks(Patient patient) {
        System.out.println("Feedbacks for " + patient.getName() + ":");
        for (Feedback f : patient.getFeedbacks()) {
            System.out.println(f);
        }
    }

    // viewing patient vitals
    public void viewPatientVitals(Patient patient) {
        System.out.println("Vitals for " + patient.getName() + ":");
        System.out.println(patient.getVitals());
    }

    // approving appointments
    public void approveAppointment(Appointment appointment) {
        AppointmentManager.approveAppointment(appointment);
        System.out.println("Appointment approved for: " + appointment.getPatient().getName());
    }

    // cancellign appointments
    public void cancelAppointment(Appointment appointment) {
        AppointmentManager.cancelAppointment(appointment);
        System.out.println("Appointment cancelled for: " + appointment.getPatient().getName());
    }

    public Appointment[] getAppointments() {
        List<Appointment> appointments = AppointmentManager.getAppointments();
        List<Appointment> doctorAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor().equals(this)) {
                doctorAppointments.add(appointment);
            }
        }
        return doctorAppointments.toArray(new Appointment[0]);
    }
}
