package com.example.rpms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Administrator extends User {
    private static final String INSERT_DOCTOR = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'DOCTOR')";
    private static final String INSERT_PATIENT = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'PATIENT')";

    public Administrator(String id, String name, String email) {
        super(id, name, email);
    }

    public void registerDoctor(Doctor doctor) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_DOCTOR, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, doctor.getName());
            stmt.setString(2, "defaultPassword"); // Should be hashed in production
            stmt.setString(3, doctor.getEmail());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Get the generated user_id
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        insertDoctorDetails(userId, doctor);
                        addSystemLog("Doctor registered: " + doctor.getName());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertDoctorDetails(int userId, Doctor doctor) {
        String sql = "INSERT INTO patient_details (doctor_id, specialization, phone_number) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getPhoneNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Doctor> getDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT u.*, d.specialization, d.phone_number FROM users u " +
                    "JOIN doctor_details d ON u.user_id = d.doctor_id " +
                    "WHERE u.role = 'DOCTOR'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Doctor doctor = new Doctor(
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("username"),
                    rs.getString("email")
                );
                doctor.setSpecialization(rs.getString("specialization"));
                doctor.setPhoneNumber(rs.getString("phone_number"));
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public void removeDoctor(String doctorId) {
        String sql = "DELETE FROM users WHERE user_id = ? AND role = 'DOCTOR'";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctorId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                addSystemLog("Doctor removed: ID " + doctorId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addSystemLog(String log) {
        String sql = "INSERT INTO emergency_alerts (alert_type, alert_message) VALUES ('SYSTEM_LOG', ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, log);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSystemLogs() {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT alert_message FROM emergency_alerts WHERE alert_type = 'SYSTEM_LOG' ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(rs.getString("alert_message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}