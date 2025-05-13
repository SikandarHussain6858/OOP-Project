package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
            showError("Load Doctor Info Error", "Error loading doctor information: " + e.getMessage());
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
            showError("Patient Data View Error", "Error loading patient data view: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewFeedback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/view_patient_feedback.fxml"));
            Parent root = loader.load();
            ViewPatientFeedbackController controller = loader.getController();
            controller.setDoctorId(doctorId);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Patient Feedback");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Feedback View Error", "Error loading feedback view: " + e.getMessage());
        }
    }

    @FXML
    private void handleScheduleAppointments() {
        loadContent("bookAppointment", "Book Appointment");
    }

    @FXML
    private void handleVideoConsultation() {
        loadContent("video_consultation", "Video Consultation");
    }

    @FXML
    private void handleViewAppointments() {
        loadContent("ViewAppointments", "Appointments");
    }

    @FXML
    private void handleEmergencyAlerts() {
        try {            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/EmergencyAlerts.fxml"));
            Parent root = loader.load();
            EmergencyAlertController controller = loader.getController();
            controller.setDoctorId(doctorId);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Emergency Alerts");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Emergency Alerts Error", "Error loading emergency alerts: " + e.getMessage());
        }
          
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
            showError("Login Error", "Error loading login page: " + e.getMessage());
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
                } else if (controller instanceof DoctorProfileController) {
                    ((DoctorProfileController) controller).setDoctorId(doctorId);
                } else if (controller instanceof DoctorViewPatientsController) {
                    ((DoctorViewPatientsController) controller).setDoctorId(doctorId);
                } else if (controller instanceof EmailController) {
                    ((EmailController) controller).setUserId(doctorId);
                }
            }

            if (content != null) {
                mainContent.getChildren().clear();
                mainContent.getChildren().add(content);
            } else {
                showError("Error", "Failed to load content for " + title);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError(title, "Error loading " + title + ": " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
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
