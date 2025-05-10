package com.example.rpms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.rpms.model.DatabaseConnector;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Admin", "Doctor", "Patient");
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            errorLabel.setText("⚠️ All fields are required.");
            errorLabel.setVisible(true);
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT user_id, role FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.toUpperCase());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String userId = rs.getString("user_id");
                String userRole = rs.getString("role");
                
                switch (userRole) {
                    case "PATIENT" -> loadPatientDashboard(userId);
                    case "DOCTOR" -> loadDoctorDashboard(userId);
                    case "ADMIN" -> loadAdminDashboard();
                    default -> {
                        errorLabel.setText("❌ Invalid role.");
                        errorLabel.setVisible(true);
                    }
                }
            } else {
                errorLabel.setText("❌ Invalid credentials.");
                errorLabel.setVisible(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("❌ Database error.");
            errorLabel.setVisible(true);
        }
    }

    private void loadPatientDashboard(String patientId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/patient_dashboard.fxml"));
            Parent root = loader.load();

            PatientDashboardController controller = loader.getController();
            controller.setPatientId(patientId);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Patient Dashboard");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("❌ Could not load patient dashboard.");
            errorLabel.setVisible(true);
        }
    }

    private void loadDoctorDashboard(String doctorId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/doctor_dashboard.fxml"));
            Parent root = loader.load();
            
            DoctorDashboardController controller = loader.getController();
            controller.setDoctorId(doctorId);


            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Doctor Dashboard");
            stage.show();
        } catch (IOException e) {
            showError("Could not load doctor dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);
    }

    private void loadAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/admin_dashboard.fxml"));
            Parent root = loader.load();

            AdminDashboardController controller = loader.getController();
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace(); // This will help with debugging
            showError("Could not load admin dashboard: " + e.getMessage());
        }
    }

    @FXML
    public void handleForgotPassword(javafx.scene.input.MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Password Recovery");
        alert.setContentText("Please contact support:\nEmail: support@hospital.com\nPhone: +123-456-7890");
        alert.showAndWait();
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/sign_up.fxml"));
            Parent signupRoot = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(signupRoot));
            stage.setTitle("Sign Up");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
