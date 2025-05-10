package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import com.example.rpms.model.DatabaseConnector;

public class UploadVitalsController {

    @FXML private ComboBox<String> heartRateComboBox;
    @FXML private ComboBox<String> oxygenLevelComboBox;
    @FXML private ComboBox<String> systolicComboBox;
    @FXML private ComboBox<String> diastolicComboBox;
    @FXML private ComboBox<String> temperatureComboBox;
    @FXML private Label messageLabel;

    private String patientId;

    @FXML
    public void initialize() {
        // Initialize heart rate ranges (60-100 bpm is normal)
        heartRateComboBox.setItems(FXCollections.observableArrayList(
            "Below 60", "60-70", "71-80", "81-90", "91-100", "Above 100"
        ));

        // Initialize oxygen levels (95-100% is normal)
        oxygenLevelComboBox.setItems(FXCollections.observableArrayList(
            "Below 90", "90-92", "93-95", "96-98", "99-100"
        ));

        // Initialize blood pressure ranges
        systolicComboBox.setItems(FXCollections.observableArrayList(
            "Below 120", "120-129", "130-139", "140-159", "160-180", "Above 180"
        ));
        diastolicComboBox.setItems(FXCollections.observableArrayList(
            "Below 80", "80-84", "85-89", "90-99", "100-120", "Above 120"
        ));

        // Initialize temperature ranges (36.1-37.2°C is normal)
        temperatureComboBox.setItems(FXCollections.observableArrayList(
            "Below 35.0", "35.0-36.0", "36.1-37.2", "37.3-38.0", "Above 38.0"
        ));
    }

    public void setPatientId(String id) {
        this.patientId = id;
    }

    @FXML
    private void handleUploadVitals() {
        if (!validateInputs()) {
            return; // Stop processing if validation fails
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO patient_vitals (patient_id, heart_rate, oxygen_saturation, " +
                         "bp_systolic, bp_diastolic, temperature, recorded_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                stmt.setDouble(2, extractHeartRate(heartRateComboBox.getValue()));
                stmt.setDouble(3, extractOxygenLevel(oxygenLevelComboBox.getValue()));
                stmt.setDouble(4, extractSystolic(systolicComboBox.getValue()));
                stmt.setDouble(5, extractDiastolic(diastolicComboBox.getValue()));
                stmt.setDouble(6, extractTemperature(temperatureComboBox.getValue()));
                stmt.setObject(7, LocalDateTime.now());

                int result = stmt.executeUpdate();
                if (result > 0) {
                    showMessage("Vitals uploaded successfully!", false);
                    clearFields();
                } else {
                    showMessage("Failed to upload vitals.", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Error uploading vitals: " + e.getMessage(), true);
        }
    }

    private boolean validateInputs() {
        if (heartRateComboBox.getValue() == null) {
            showMessage("Please select a heart rate range.", true);
            return false;
        }
        if (oxygenLevelComboBox.getValue() == null) {
            showMessage("Please select an oxygen level range.", true);
            return false;
        }
        if (systolicComboBox.getValue() == null) {
            showMessage("Please select a systolic blood pressure range.", true);
            return false;
        }
        if (diastolicComboBox.getValue() == null) {
            showMessage("Please select a diastolic blood pressure range.", true);
            return false;
        }
        if (temperatureComboBox.getValue() == null) {
            showMessage("Please select a temperature range.", true);
            return false;
        }
        return true;
    }

    private void clearFields() {
        heartRateComboBox.setValue(null);
        oxygenLevelComboBox.setValue(null);
        systolicComboBox.setValue(null);
        diastolicComboBox.setValue(null);
        temperatureComboBox.setValue(null);
    }

    private void showMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green"));
        messageLabel.setVisible(true);
    }

    // Helper methods to extract numeric values from combo box selections
    private double extractHeartRate(String value) {
        if (value == null || value.isEmpty()) return 0; // Default value for null or empty
        if (value.startsWith("Below")) return 59;
        if (value.startsWith("Above")) return 101;
        String[] range = value.split("-");
        return (Double.parseDouble(range[0]) + Double.parseDouble(range[1])) / 2;
    }

    private double extractOxygenLevel(String value) {
        if (value == null || value.isEmpty()) return 0; // Default value for null or empty
        if (value.startsWith("Below")) return 89;
        String[] range = value.split("-");
        return (Double.parseDouble(range[0]) + Double.parseDouble(range[1])) / 2;
    }

    private double extractSystolic(String value) {
        if (value == null || value.isEmpty()) return 0; // Default value for null or empty
        if (value.startsWith("Below")) return 119;
        if (value.startsWith("Above")) return 181;
        String[] range = value.split("-");
        return (Double.parseDouble(range[0]) + Double.parseDouble(range[1])) / 2;
    }

    private double extractDiastolic(String value) {
        if (value == null || value.isEmpty()) return 0; // Default value for null or empty
        if (value.startsWith("Below")) return 79;
        if (value.startsWith("Above")) return 121;
        String[] range = value.split("-");
        return (Double.parseDouble(range[0]) + Double.parseDouble(range[1])) / 2;
    }

    private double extractTemperature(String value) {
        if (value == null || value.isEmpty()) return 0; // Default value for null or empty
        if (value.startsWith("Below")) return 34.9;
        if (value.startsWith("Above")) return 38.1;
        String[] range = value.split("-");
        return (Double.parseDouble(range[0]) + Double.parseDouble(range[1])) / 2;
    }
}