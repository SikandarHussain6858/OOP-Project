package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DoctorDashboardController {

    @FXML
    private VBox mainContent;

    @FXML
    private void handleViewPatientData() {
        showInfo("View Patient Data", "This would display patient vitals and health info.");
    }

    @FXML
    private void handleGiveFeedback() {
        showInfo("Give Feedback", "This would allow you to write feedback for a patient.");
    }

    @FXML
    private void handleScheduleAppointments() {
        showInfo("Schedule Appointments", "This would allow you to schedule a new appointment.");
    }

    @FXML
    private void handleViewHistory() {
        showInfo("Patient History", "This would display the patient's medical history.");
    }

    @FXML
    private void handleEmergencyAlerts() {
        showInfo("Emergency Alerts", "This would show recent emergency alerts from patients.");
    }

    @FXML
    private void handleViewProfile() {
        showInfo("My Profile", "This would show the doctor's profile and editable details.");
    }

    @FXML
    private void handleLogout() {
        showInfo("Logout", "You have been logged out.");
        // You can close the stage and return to login here
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
