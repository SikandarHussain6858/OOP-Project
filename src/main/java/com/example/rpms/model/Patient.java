package com.example.rpms.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Patient extends User {
    private LocalDate dob;
    private String gender;
    private String contact;
    private String address;
    private String medicalHistory;
    private List<Vitals> vitals;
    private List<Feedback> feedbacks;

    // Constructor
    public Patient(String id, String name, String email) {  // Changed from int to String for id
        super(id, name, email);
        loadFromDatabase();
        loadVitals();
        loadFeedbacks();
    }

    // Calculate age from DOB
    public int getAge() {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    // Getters and Setters
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    
    public List<Vitals> getVitals() { return vitals; }
    public List<Feedback> getFeedbacks() { return feedbacks; }

    // Load patient details from database
    private void loadFromDatabase() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT * FROM patient_details WHERE patient_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, getId());  // Changed from setInt to setString
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    this.dob = rs.getDate("dob").toLocalDate();
                    this.gender = rs.getString("gender");
                    this.contact = rs.getString("contact");
                    this.address = rs.getString("address");
                    this.medicalHistory = rs.getString("medical_history");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save patient details to database
    public boolean saveToDatabase() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "UPDATE patient_details SET dob = ?, gender = ?, contact = ?, " +
                        "address = ?, medical_history = ? WHERE patient_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(dob));
                stmt.setString(2, gender);
                stmt.setString(3, contact);
                stmt.setString(4, address);
                stmt.setString(5, medicalHistory);
                stmt.setString(6, getId());  // Changed from setInt to setString
                
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadVitals() {
        vitals = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM patient_vitals WHERE patient_id = ? ORDER BY recorded_at DESC")) {

            stmt.setString(1, getId());  // Changed from setInt to setString
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                vitals.add(new Vitals(
                    rs.getInt("vital_id"),
                    rs.getInt("patient_id"),
                    rs.getDouble("bp_systolic"),
                    rs.getDouble("bp_diastolic"),
                    rs.getDouble("heart_rate"),
                    rs.getDouble("temperature"),
                    rs.getDouble("oxygen_saturation"),
                    rs.getDouble("glucose"),
                    rs.getTimestamp("recorded_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadFeedbacks() {
        feedbacks = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM doctor_feedback WHERE patient_id = ? ORDER BY created_at DESC")) {

            stmt.setString(1, getId());  // Changed from setInt to setString
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                feedbacks.add(new Feedback(
                    rs.getInt("feedback_id"),
                    rs.getInt("patient_id"),
                    rs.getString("comments"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add feedback
    public void addFeedback(Feedback feedback) {
        if (feedbacks == null) {
            feedbacks = new ArrayList<>();
        }
        feedbacks.add(feedback);
    }
}
