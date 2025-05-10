package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import com.example.rpms.model.DatabaseConnector;
import java.time.LocalDateTime;
import javafx.stage.Stage;

public class WritePrescriptionController {
    @FXML private TextArea diagnosisTextArea;
    @FXML private TextArea medicationsTextArea;
    @FXML private TextArea instructionsTextArea;
    @FXML private Label patientNameLabel;
    @FXML private Label dateLabel;
    @FXML private Label statusLabel;
    
    private String patientId;
    private String doctorId;
    private String patientName;

    @FXML
    public void initialize() {
        dateLabel.setText(LocalDateTime.now().toString().split("T")[0]);
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setPatientName(String name) {
        this.patientName = name;
        patientNameLabel.setText(name);
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                INSERT INTO prescriptions (patient_id, doctor_id, diagnosis, medications, 
                instructions, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                stmt.setString(2, doctorId);
                stmt.setString(3, diagnosisTextArea.getText().trim());
                stmt.setString(4, medicationsTextArea.getText().trim());
                stmt.setString(5, instructionsTextArea.getText().trim());
                stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

                int result = stmt.executeUpdate();
                if (result > 0) {
                    showSuccess("Prescription saved successfully");
                    // Close the window after successful save
                    ((Stage) diagnosisTextArea.getScene().getWindow()).close();
                } else {
                    showError("Failed to save prescription");
                }
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) diagnosisTextArea.getScene().getWindow()).close();
    }

    private boolean validateInputs() {
        String diagnosis = diagnosisTextArea.getText().trim();
        String medications = medicationsTextArea.getText().trim();
        String instructions = instructionsTextArea.getText().trim();

        if (diagnosis.isEmpty()) {
            showError("Please enter diagnosis");
            return false;
        }

        if (medications.isEmpty()) {
            showError("Please enter medications");
            return false;
        }

        if (instructions.isEmpty()) {
            showError("Please enter instructions");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
    }
}
