package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorDashboardController{
    @FXML
    private VBox mainContent;

    private void loadDoctorInfo() {
        try {
            // Load doctor information from database using userId
            // Update UI elements accordingly
        } catch (Exception e) {
            showError("Error loading doctor information: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewPatientData() {
        try {
            FXMLLoader loader = new FXMLLoader(DoctorDashboardController.class.getResource("/com/example/rpms/fxml/viewPatientData.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Patient Data");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error loading patient data view: " + e.getMessage());
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/DoctorProfile.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Doctor Profile");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
