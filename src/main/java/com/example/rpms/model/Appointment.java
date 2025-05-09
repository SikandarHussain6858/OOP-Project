package com.example.rpms.model;

import java.sql.*;
import java.time.LocalDateTime;

public class Appointment {
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private LocalDateTime appointmentDate;
    private String status;
    private String notes;

    public Appointment(int patientId, int doctorId, LocalDateTime appointmentDate, String notes) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.status = "PENDING";
        this.notes = notes;
    }

    public boolean saveToDatabase() {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, status, notes) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, patientId);
            stmt.setInt(2, doctorId);
            stmt.setTimestamp(3, Timestamp.valueOf(appointmentDate));
            stmt.setString(4, status);
            stmt.setString(5, notes);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    this.appointmentId = rs.getInt(1);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(String newStatus) {
        if (!isValidStatus(newStatus)) {
            return false;
        }

        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, appointmentId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidStatus(String status) {
        return status != null && (
            status.equals("PENDING") || 
            status.equals("APPROVED") || 
            status.equals("CANCELLED")
        );
    }

    public static Appointment loadFromDatabase(int appointmentId) {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Appointment appointment = new Appointment(
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getTimestamp("appointment_date").toLocalDateTime(),
                    rs.getString("notes")
                );
                appointment.appointmentId = appointmentId;
                appointment.status = rs.getString("status");
                return appointment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
}