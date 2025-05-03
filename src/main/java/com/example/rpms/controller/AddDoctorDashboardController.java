package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class AddDoctorDashboardController {

    @FXML private TextField nameField;
    @FXML private TextField specializationField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dobPicker;
    @FXML private TextArea addressArea;
    @FXML private TextField qualificationField;
    @FXML private TextField experienceField;
    @FXML private CheckBox monCheck, tueCheck, wedCheck, thuCheck, friCheck;
    @FXML private TextField timeSlotField;
    @FXML private Label statusLabel;

    @FXML
    private void handleAddDoctor() {
        String name = nameField.getText();
        String specialization = specializationField.getText();
        String email = emailField.getText();
        String phone = phoneNumberField.getText();
        String gender = genderComboBox.getValue();
        LocalDate dob = dobPicker.getValue();
        String address = addressArea.getText();
        String qualification = qualificationField.getText();
        String experience = experienceField.getText();
        String timeSlot = timeSlotField.getText();

        StringBuilder availableDays = new StringBuilder();
        if (monCheck.isSelected()) availableDays.append("Mon ");
        if (tueCheck.isSelected()) availableDays.append("Tue ");
        if (wedCheck.isSelected()) availableDays.append("Wed ");
        if (thuCheck.isSelected()) availableDays.append("Thu ");
        if (friCheck.isSelected()) availableDays.append("Fri ");

        // Simple Validation
        if (name.isEmpty() || specialization.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            statusLabel.setText("❌ Please fill all required fields!");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
            return;
        }

        // Simulate database save
        System.out.println("✅ Doctor added:");
        System.out.println("Name: " + name);
        System.out.println("Specialization: " + specialization);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
        System.out.println("Gender: " + gender);
        System.out.println("DOB: " + dob);
        System.out.println("Address: " + address);
        System.out.println("Qualification: " + qualification);
        System.out.println("Experience: " + experience);
        System.out.println("Available Days: " + availableDays.toString().trim());
        System.out.println("Time Slot: " + timeSlot);

        statusLabel.setText("✅ Doctor successfully added!");
        statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

        // Optional: clear fields
        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        specializationField.clear();
        emailField.clear();
        phoneNumberField.clear();
        genderComboBox.setValue(null);
        dobPicker.setValue(null);
        addressArea.clear();
        qualificationField.clear();
        experienceField.clear();
        monCheck.setSelected(false);
        tueCheck.setSelected(false);
        wedCheck.setSelected(false);
        thuCheck.setSelected(false);
        friCheck.setSelected(false);
        timeSlotField.clear();
    }
}
