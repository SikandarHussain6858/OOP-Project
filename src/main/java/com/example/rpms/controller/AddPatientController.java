package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class AddPatientController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dobPicker;
    @FXML private TextArea addressArea;
    @FXML private TextField emergencyContactField;
    @FXML private TextArea medicalHistoryArea;
    @FXML private TextField allergiesField;
    @FXML private ComboBox<String> patientTypeComboBox;
    @FXML private Label statusLabel;

    @FXML
    private void handleAddPatient() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String gender = genderComboBox.getValue();
        LocalDate dob = dobPicker.getValue();
        String address = addressArea.getText();
        String emergencyContact = emergencyContactField.getText();
        String medicalHistory = medicalHistoryArea.getText();
        String allergies = allergiesField.getText();
        String patientType = patientTypeComboBox.getValue();

        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || emergencyContact.isEmpty()) {
            statusLabel.setText("❌ Please fill all required fields!");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        // Simulate saving to database
        System.out.println("✅ Patient added:");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("Gender: " + gender);
        System.out.println("DOB: " + dob);
        System.out.println("Address: " + address);
        System.out.println("Emergency Contact: " + emergencyContact);
        System.out.println("Medical History: " + medicalHistory);
        System.out.println("Allergies: " + allergies);
        System.out.println("Patient Type: " + patientType);

        statusLabel.setText("✅ Patient successfully added!");
        statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        genderComboBox.setValue(null);
        dobPicker.setValue(null);
        addressArea.clear();
        emergencyContactField.clear();
        medicalHistoryArea.clear();
        allergiesField.clear();
        patientTypeComboBox.setValue(null);
    }
}
