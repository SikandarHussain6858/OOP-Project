package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PatientDashboardController {

    @FXML
    private VBox mainContent;

    @FXML
    private void handleUploadVitals() {
        mainContent.getChildren().clear();
        Label label = new Label("Vitals Upload Section");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        mainContent.getChildren().add(label);
        // Add form fields here later
    }

    @FXML
    private void handleViewReports() {
        mainContent.getChildren().clear();
        Label label = new Label("Reports Viewer");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        mainContent.getChildren().add(label);
        // Add report list/table later
    }

    @FXML
    private void handleDoctorFeedback() {
        mainContent.getChildren().clear();
        Label label = new Label("Doctor Feedback Section");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        mainContent.getChildren().add(label);
        // Add feedback view logic later
    }

    @FXML
    private void handleEmergencyAlert() {
        mainContent.getChildren().clear();
        Label label = new Label("Emergency Alert Sent");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
        mainContent.getChildren().add(label);
        // Add alert system later
    }

    @FXML
    private void handleViewProfile() {
        mainContent.getChildren().clear();
        Label label = new Label("My Profile Section");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
        mainContent.getChildren().add(label);
        // Add editable profile details later
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Logout");
        alert.setHeaderText(null);
        alert.setContentText("You have been logged out.");
        alert.showAndWait();
        // Add logic to close dashboard and return to login screen if needed
    }
}
