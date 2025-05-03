package com.example.rpms.controller;

import com.example.rpms.model.Doctor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RemoveDoctorDashboardController {

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleRemoveDoctor() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            statusLabel.setText("⚠️ All fields are required.");
            statusLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        boolean removed = Doctor.removeDoctorByIdNameEmail(id, name, email);

        if (removed) {
            statusLabel.setText("✅ Doctor removed successfully.");
            statusLabel.setStyle("-fx-text-fill: green;");
        } else {
            statusLabel.setText("❌ Doctor not found.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
