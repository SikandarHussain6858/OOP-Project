package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DoctorDashboardController {

    @FXML
    private Label doctorLabel;

    @FXML
    public void initialize() {
        doctorLabel.setText("Welcome to Doctor Dashboard!");
    }
}
