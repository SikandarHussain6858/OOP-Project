package com.example.rpms.controller;

import com.example.rpms.model.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RemovePatientController {

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleRemovePatient() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            statusLabel.setText("⚠️ All fields are required.");
            statusLabel.setStyle("-fx-text-fill: orange;");
            return;
        }

        boolean removed = Patient.removePatientByIdNameEmail(id, name, email);

        if (removed) {
            statusLabel.setText("✅ Patient removed successfully.");
            statusLabel.setStyle("-fx-text-fill: green;");
        } else {
            statusLabel.setText("❌ Patient not found.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}

