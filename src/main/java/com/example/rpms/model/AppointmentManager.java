package com.example.rpms.model;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentManager {
    
    public static List<Appointment> getAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY appointment_date";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getTimestamp("appointment_date").toLocalDateTime(),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public static List<Appointment> getPendingAppointments() {
        return getAppointmentsByStatus("PENDING");
    }

    public static List<Appointment> getApprovedAppointments() {
        return getAppointmentsByStatus("APPROVED");
    }

    public static List<Appointment> getCancelledAppointments() {
        return getAppointmentsByStatus("CANCELLED");
    }

    private static List<Appointment> getAppointmentsByStatus(String status) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE status = ? ORDER BY appointment_date";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getTimestamp("appointment_date").toLocalDateTime(),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public static boolean requestAppointment(Appointment appointment) {
        return appointment.saveToDatabase();
    }

    public static boolean approveAppointment(int appointmentId) {
        Appointment appointment = Appointment.loadFromDatabase(appointmentId);
        if (appointment != null) {
            return appointment.updateStatus("APPROVED");
        }
        return false;
    }

    public static boolean cancelAppointment(int appointmentId) {
        Appointment appointment = Appointment.loadFromDatabase(appointmentId);
        if (appointment != null) {
            return appointment.updateStatus("CANCELLED");
        }
        return false;
    }

    public static List<Appointment> getDoctorAppointments(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_date";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getTimestamp("appointment_date").toLocalDateTime(),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public static List<Appointment> getPatientAppointments(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                appointments.add(new Appointment(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getTimestamp("appointment_date").toLocalDateTime(),
                    rs.getString("notes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
}

