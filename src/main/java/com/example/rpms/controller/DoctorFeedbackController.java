package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDateTime;

public class DoctorFeedbackController {
    @FXML private TableView<FeedbackEntry> feedbackTable;
    @FXML private TableColumn<FeedbackEntry, LocalDateTime> dateColumn;
    @FXML private TableColumn<FeedbackEntry, String> doctorColumn;
    @FXML private TableColumn<FeedbackEntry, String> feedbackColumn;
    @FXML private ComboBox<DoctorEntry> doctorComboBox;
    @FXML private TextArea feedbackArea;

    private String patientId;

    @FXML
    public void initialize() {
        // Initialize table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));

        // Load doctors into combo box
        loadDoctors();
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
        loadFeedbacks();
    }

    private void loadDoctors() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT u.user_id, u.username as name FROM users u " +
                        "JOIN doctor_details d ON u.user_id = d.doctor_id " +
                        "WHERE u.role = 'DOCTOR'";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                doctorComboBox.getItems().add(new DoctorEntry(
                    rs.getString("user_id"),
                    rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading doctors: " + e.getMessage());
        }
    }

    private void loadFeedbacks() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT f.date, u.name as doctor_name, f.feedback " +
                        "FROM feedbacks f " +
                        "JOIN users u ON f.doctor_id = u.user_id " +
                        "WHERE f.patient_id = ? " +
                        "ORDER BY f.date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            feedbackTable.getItems().clear();
            while (rs.next()) {
                feedbackTable.getItems().add(new FeedbackEntry(
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("doctor_name"),
                    rs.getString("feedback")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading feedbacks: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmitFeedback() {
        DoctorEntry selectedDoctor = doctorComboBox.getValue();
        String feedback = feedbackArea.getText().trim();

        if (selectedDoctor == null) {
            showError("Please select a doctor");
            return;
        }

        if (feedback.isEmpty()) {
            showError("Please enter feedback");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO feedbacks (patient_id, doctor_id, feedback, date) VALUES (?, ?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            stmt.setString(2, selectedDoctor.getId());
            stmt.setString(3, feedback);
            stmt.executeUpdate();

            // Clear form and reload feedbacks
            feedbackArea.clear();
            doctorComboBox.setValue(null);
            loadFeedbacks();

            showInfo("Feedback submitted successfully");
        } catch (SQLException e) {
            showError("Error submitting feedback: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper classes
    public static class FeedbackEntry {
        private final LocalDateTime date;
        private final String doctorName;
        private final String feedback;

        public FeedbackEntry(LocalDateTime date, String doctorName, String feedback) {
            this.date = date;
            this.doctorName = doctorName;
            this.feedback = feedback;
        }

        public LocalDateTime getDate() { return date; }
        public String getDoctorName() { return doctorName; }
        public String getFeedback() { return feedback; }
    }

    public static class DoctorEntry {
        private final String id;
        private final String name;

        public DoctorEntry(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String toString() { return name; }
    }
}