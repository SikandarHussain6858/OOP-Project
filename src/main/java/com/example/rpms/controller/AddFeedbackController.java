package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;
import com.example.rpms.model.DatabaseConnector;

public class AddFeedbackController {
    @FXML private TextArea feedbackArea;
    
    private String patientId;
    private String doctorId;

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @FXML
    private void handleSubmit() {
        String feedback = feedbackArea.getText().trim();
        
        if (feedback.isEmpty()) {
            showError("Please enter feedback");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO feedbacks (patient_id, doctor_id, feedback, date) VALUES (?, ?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            stmt.setString(2, doctorId);
            stmt.setString(3, feedback);
            stmt.executeUpdate();

            showInfo("Feedback submitted successfully");
            closeDialog();
        } catch (SQLException e) {
            showError("Error submitting feedback: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) feedbackArea.getScene().getWindow();
        stage.close();
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
}
