package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;

public class EmergencyAlertController {

    @FXML
    private Button panicButton;

    @FXML
    private void handlePanicButtonAction() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Emergency Alert");
        alert.setHeaderText("Emergency Triggered!");
        alert.setContentText("An emergency alert has been sent to your assigned doctor.");
        alert.showAndWait();
    }
}
