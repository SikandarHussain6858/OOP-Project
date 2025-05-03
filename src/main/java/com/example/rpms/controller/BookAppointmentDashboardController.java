package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;

public class BookAppointmentDashboardController {

    @FXML
    private TextField patientNameField;

    @FXML
    private TextField doctorNameField;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private TextField timeField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleBookAppointment() {
        String patientName = patientNameField.getText();
        String doctorName = doctorNameField.getText();
        LocalDate date = appointmentDatePicker.getValue();
        String time = timeField.getText();

        if (patientName.isEmpty() || doctorName.isEmpty() || date == null || time.isEmpty()) {
            statusLabel.setText("❗ Please fill in all fields.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        } else {
            // Here, you would normally add logic to save the appointment in a database
            statusLabel.setText("✅ Appointment booked successfully!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);

            // Optional: Clear fields
            patientNameField.clear();
            doctorNameField.clear();
            appointmentDatePicker.setValue(null);
            timeField.clear();
        }
    }
}

