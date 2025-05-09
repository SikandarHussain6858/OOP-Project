package com.example.rpms.controller;

import com.example.rpms.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import com.example.rpms.model.DatabaseConnector;
import com.example.rpms.model.Patient;
// Ensure the correct package path for PatientProfileController

public class PatientDashboardController {
    @FXML 
    private VBox mainContent;
    private String patientId;
    private Timer medicationReminderTimer;
    private Map<String, Stage> openWindows = new HashMap<>();

    @FXML
    public void initialize() {
        if (patientId == null || patientId.isEmpty()) {
            showError("Patient ID not set. Please log in again.");
            return;
        }
        setupMedicationReminders();
        loadPatientDashboard();
    }

    private void loadPatientDashboard() {
        VBox dashboard = new VBox(10);
        dashboard.getChildren().addAll(
            createWelcomeSection(),
            createVitalsChart(), // Ensure the method is defined below
            createUpcomingAppointments(), // Ensure this method is defined below
            createMedicationSchedule() // Ensure the method is defined below
        );
        mainContent.getChildren().setAll(dashboard);
    }

    private VBox createWelcomeSection() {
        VBox welcomeSection = new VBox();
        Label welcomeLabel = new Label("Welcome to your Dashboard!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        welcomeSection.getChildren().add(welcomeLabel);
        return welcomeSection;
    }

    private VBox createVitalsChart() {
        VBox vitalsChartSection = new VBox();
        Label vitalsChartLabel = new Label("Vitals Chart");
        vitalsChartLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        vitalsChartSection.getChildren().add(vitalsChartLabel);
        // Add logic to display vitals chart
        return vitalsChartSection;
    }

    private VBox createMedicationSchedule() {
        VBox medicationScheduleSection = new VBox();
        Label medicationScheduleLabel = new Label("Medication Schedule");
        medicationScheduleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        medicationScheduleSection.getChildren().add(medicationScheduleLabel);
        // Add logic to fetch and display medication schedule
        return medicationScheduleSection;
    }

    private VBox createUpcomingAppointments() {
        VBox appointmentsSection = new VBox();
        Label appointmentsLabel = new Label("Upcoming Appointments");
        appointmentsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        appointmentsSection.getChildren().add(appointmentsLabel);
        // Add logic to fetch and display upcoming appointments
        return appointmentsSection;
    }

    @FXML
    private void handleUploadVitals() {
        loadModalWindow("uploadVitals", "Upload Vitals");
    }

    @FXML
    private void handleViewReports() {
        loadModalWindow("viewReports", "View Reports");
    }

    @FXML
    private void handleDoctorFeedback() {
        loadModalWindow("doctorFeedback", "Doctor Feedback");
    }

    @FXML
    private void handleBookAppointment() {
        loadModalWindow("bookAppointment", "Book Appointment");
    }


    @FXML
    private void handleEmergencyAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Emergency Alert");
        alert.setHeaderText("Send Emergency Alert?");
        alert.setContentText("This will notify medical staff immediately. Continue?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sendEmergencyAlert();
            }
        });
    }

    private void sendEmergencyAlert() {
        try (var conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO emergency_alerts (patient_id, status) VALUES (?, 'ACTIVE')";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                stmt.executeUpdate();
                showInfo("Emergency alert has been sent. Medical staff will be notified.");
            }
        } catch (SQLException e) {
            showError("Failed to send emergency alert: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewProfile() {
        loadModalWindow("Patientprofile", "My Profile");
    }

    @FXML
    private void handleLogout() {
        if (medicationReminderTimer != null) {
            medicationReminderTimer.cancel();
        }
        try {
            // Close all open windows
            openWindows.values().forEach(Stage::close);
            openWindows.clear();

            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - RPMS");
        } catch (IOException e) {
            showError("Error returning to login screen: " + e.getMessage());
        }
    }

    private void loadModalWindow(String fxmlName, String title) {
        try {
            if (openWindows.containsKey(fxmlName)) {
                openWindows.get(fxmlName).requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/" + fxmlName + ".fxml"));
            Parent root = loader.load();


            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            
            openWindows.put(fxmlName, stage);
            stage.setOnCloseRequest(e -> openWindows.remove(fxmlName));
            
            stage.show();

        } catch (IOException e) {
            showError("Error loading " + title + ": " + e.getMessage());
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupMedicationReminders() {
        medicationReminderTimer = new Timer(true);
        medicationReminderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkMedicationSchedule();
            }
        }, 0, 60000);
    }

    private void checkMedicationSchedule() {
        Platform.runLater(() -> {
            // Show notification using JavaFX Alert
        });
    }
}
