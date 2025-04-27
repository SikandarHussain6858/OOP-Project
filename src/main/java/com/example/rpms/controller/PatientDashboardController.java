package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PatientDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome to Patient Dashboard!");
    }
}
