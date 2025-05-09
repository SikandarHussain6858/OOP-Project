package com.example.rpms.controller;

import com.example.rpms.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AdminDashboardController {
    @FXML
    private VBox mainContent;
    
    private Administrator administrator;
    private Map<String, Stage> openWindows;

    @FXML
    public void initialize() {
        // Initialize administrator with default admin credentials
        administrator = new Administrator("1", "Admin", "admin@rpms.com");
        openWindows = new HashMap<>();
        
        validateDatabaseConnection();
    }

    private void validateDatabaseConnection() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            showError("Database Connection Error", 
                     "Failed to connect to database: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddPatient() {
        loadForm("addPatientform", "Add Patient");
    }

    @FXML
    private void handleRemovePatient(ActionEvent event) {
        loadForm("removePatient", "Remove Patient");
    }

    @FXML
    private void handleAddDoctor() {
        loadForm("addDoctorform", "Add Doctor");
    }

    @FXML
    private void handleRemoveDoctor(ActionEvent event) {
        loadForm("removeDoctor", "Remove Doctor");
    }

    @FXML
    private void handleBookAppointment() {
        loadForm("bookAppointment", "Book Appointment");
    }

    @FXML
    private void handleViewPatients() {
        loadForm("ViewPatients", "View Patients");
    }

    @FXML
    private void handleViewDoctors() {
        loadForm("ViewDoctors", "View Doctors");
    }

    @FXML
    private void handleSendEmail() {
        loadForm("email", "Send Email");
    }

    @FXML
    private void handleEmergencyAlerts() {
        try {
            if (openWindows.containsKey("emergencyAlerts")) {
                openWindows.get("emergencyAlerts").requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/rpms/fxml/emergencyAlerts.fxml"));
            Parent root = loader.load();

            EmergencyAlertController controller = loader.getController();
            controller.loadActiveAlerts();

            Stage stage = createStage("Emergency Alerts", root);
            openWindows.put("emergencyAlerts", stage);
            
            stage.setOnCloseRequest(e -> openWindows.remove("emergencyAlerts"));
            stage.show();
        } catch (IOException e) {
            showError("Error", "Could not load emergency alerts: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewAppointments() {
        loadForm("ViewAppointments", "View Appointments");
    }

    @FXML
    private void handleSystemReports() {
        loadForm("systemReports", "System Reports");
    }

    private void loadForm(String fxmlName, String title) {
        try {
            if (openWindows.containsKey(fxmlName)) {
                openWindows.get(fxmlName).requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/rpms/fxml/" + fxmlName + ".fxml"));
            Parent root = loader.load();

            Stage stage = createStage(title, root);
            openWindows.put(fxmlName, stage);
            
            stage.setOnCloseRequest(e -> openWindows.remove(fxmlName));
            stage.show();
        } catch (IOException e) {
            showError("Error", "Could not load " + title + ": " + e.getMessage());
        }
    }

    private Stage createStage(String title, Parent root) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainContent.getScene().getWindow());
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        return stage;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Close all open windows
            openWindows.values().forEach(Stage::close);
            openWindows.clear();

            // Load the login screen
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/rpms/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage currentStage = (Stage) mainContent.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Login - RPMS");
            
            administrator.addSystemLog("Admin logged out");
        } catch (IOException e) {
            showError("Logout Error", "Could not return to login screen: " + e.getMessage());
        }
    }
}
