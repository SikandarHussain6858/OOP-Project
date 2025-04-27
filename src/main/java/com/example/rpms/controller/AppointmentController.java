package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AppointmentController {

    @FXML
    private Label appointmentLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private void handleConfirmButtonAction() {
        appointmentLabel.setText("Appointment Confirmed!");
    }
}
