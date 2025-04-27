package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private Label adminLabel;

    @FXML
    public void initialize() {
        adminLabel.setText("Welcome to Admin Dashboard!");
    }
}
