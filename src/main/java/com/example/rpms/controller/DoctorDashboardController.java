package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorDashboardController {
    @FXML
    private VBox mainContent;
    private String doctorId;

    public void setDoctorId(String id) {
        this.doctorId = id;
        loadDoctorInfo();
    }

    private void loadDoctorInfo() {
        try {
            // Load doctor information from database using doctorId
            // Update UI elements accordingly
        } catch (Exception e) {
            showError("Error loading doctor information: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewPatientData() {
        try {
            FXMLLoader loader = new FXMLLoader(DoctorDashboardController.class.getResource("/com/example/rpms/fxml/doctor_view_patients.fxml"));
            Parent root = loader.load();
            DoctorViewPatientsController controller = loader.getController();
            controller.setDoctorId(doctorId);
            Stage stage = new Stage();
            stage.setTitle("My Patients");
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
    private void handleWritePrescription() {
        loadContent("write_prescription", "Write Prescription");
    }

    @FXML
    private void handleVideoConsultation() {
        loadContent("video_consultation", "Video Consultation");
    }

    @FXML
    private void handleViewPatients() {
        loadContent("doctor_view_patients", "My Patients");
    }

    @FXML
    private void handleViewAppointments() {
        loadContent("ViewAppointments", "Appointments");
    }    @FXML
    private void handleEmergencyAlerts() {
        loadContent("doctor_emergency_alerts", "Emergency Alerts");
    }

    @FXML
    private void handleMessages() {
        loadContent("email", "Messages");
    }

    @FXML
    private void handleViewProfile() {
        loadContent("doctor_profile", "My Profile");
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Error loading login page: " + e.getMessage());
        }
    }

    private void loadContent(String fxmlName, String title) {
        try {
            System.out.println("Loading FXML: " + fxmlName);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/" + fxmlName + ".fxml"));
            Parent content = loader.load();
            Object controller = loader.getController();
            System.out.println("Controller loaded: " + (controller != null ? controller.getClass().getSimpleName() : "null"));
            
            if (controller != null) {
                if (controller instanceof ViewAppointmentsController) {
                    ((ViewAppointmentsController) controller).loadAppointmentsForDoctor(doctorId);
                } else if (controller instanceof DoctorEmergencyAlertController) {
                    ((DoctorEmergencyAlertController) controller).setDoctorId(doctorId);                } else if (controller instanceof DoctorEmergencyAlertController) {
                    ((DoctorEmergencyAlertController) controller).setDoctorId(doctorId);
                } else if (controller instanceof DoctorProfileController) {
                    ((DoctorProfileController) controller).setDoctorId(doctorId);
                } else if (controller instanceof DoctorViewPatientsController) {
                    ((DoctorViewPatientsController) controller).setDoctorId(doctorId);
                } else if (controller instanceof EmailController) {
                    ((EmailController) controller).setUserId(doctorId);
                } else if (controller instanceof WritePrescriptionController) {
                    ((WritePrescriptionController) controller).setDoctorId(doctorId);
                }
            }

            mainContent.getChildren().clear();
            mainContent.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
