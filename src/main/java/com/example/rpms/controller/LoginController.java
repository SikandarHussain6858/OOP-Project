package com.example.rpms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        // Dummy login check (replace with actual logic)
        if (role != null && role.equals("Admin") && username.equals("admin") && password.equals("admin123")) {
            errorLabel.setVisible(false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/admin_dashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (role != null && role.equals("Doctor") && username.equals("doctor") && password.equals("doc123")) {
            errorLabel.setVisible(false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/doctor_dashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (role != null && role.equals("Patient") && username.equals("patient") && password.equals("pat123")) {
            errorLabel.setVisible(false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/patient_dashboard.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            // This else was incorrectly written before
            errorLabel.setText("Invalid credentials. Please try again.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void handleForgotPassword(javafx.scene.input.MouseEvent mouseEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Password Recovery Instructions");
        alert.setContentText("Please contact the hospital administration to reset your password.\n\nEmail: support@hospital.com\nPhone: +123-456-7890");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
