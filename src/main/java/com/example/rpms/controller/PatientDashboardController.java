package com.example.rpms.controller;

import com.example.rpms.model.Patient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PatientDashboardController {

    @FXML
    private VBox mainContent;

    private String patientId;

    public void setPatientId(String id) {
        this.patientId = id;
    }

    @FXML
    private void handleUploadVitals() {
        loadContent("uploadVitals", "Upload Vitals");
    }

    @FXML
    private void handleViewReports() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/viewReport.fxml"));
            Parent root = loader.load();
            
            ViewReportController controller = loader.getController();
            controller.setPatientId(patientId);
            
            Stage stage = new Stage();
            stage.setTitle("Patient Reports");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading view reports: " + e.getMessage());
        }
    }

    @FXML
    private void handleDoctorFeedback() {
        loadContent("doctorFeedback", "Doctor Feedback");
    }    @FXML
    private void handleEmergencyAlert() {
        loadContent("EmergencyAlerts", "Emergency Alert");
    }

    @FXML
    private void handleViewProfile() {
        loadContent("Patientprofile", "Patient Profile");
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

    @FXML
    private void handleChatWithDoctor() {
        loadContent("email", "Chat with Doctor");
    }

    @FXML
    private void handleViewAppointments() {
        loadContent("ViewAppointments", "View Appointments");
    }

    @FXML
    private void handleViewPrescriptions() {
        loadContent("view_prescriptions", "View Prescriptions");
    }

    @FXML
    private void handleVideoConsultations() {
        loadContent("patient_video_consultations", "Video Consultations");
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
                    ((ViewAppointmentsController) controller).loadAppointments(patientId);
                } else if (controller instanceof DoctorFeedbackController) {
                    ((DoctorFeedbackController) controller).setPatientId(patientId);
                } else if (controller instanceof BookAppointmentController) {
                    ((BookAppointmentController) controller).setPatientId(patientId);
                } else if (controller instanceof EmergencyAlertController) {
                    ((EmergencyAlertController) controller).setPatientId(patientId);
                } else if (controller instanceof ViewReportController) {
                    ((ViewReportController) controller).setPatientId(patientId);
                } else if (controller instanceof ViewPrescriptionsController) {
                    ((ViewPrescriptionsController) controller).setPatientId(patientId);
                } else if (controller instanceof PatientVideoConsultationsController) {
                    ((PatientVideoConsultationsController) controller).setPatientId(patientId);
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

    @FXML
    public void initialize() {
        System.out.println("PatientDashboardController initialized successfully.");
    }
}
