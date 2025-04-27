package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check for Admin login
        if ("admin".equals(username) && "admin".equals(password)) {
            showAlert("Login Successful", "Welcome Admin!");
            // Redirect to Admin dashboard
        }
        // Check for Doctor login
        else if ("doctor".equals(username) && "doctor123".equals(password)) {
            showAlert("Login Successful", "Welcome Doctor!");
            // Redirect to Doctor dashboard
        }
        // Check for Patient login
        else if ("patient".equals(username) && "patient123".equals(password)) {
            showAlert("Login Successful", "Welcome Patient!");
            // Redirect to Patient dashboard
        }
        // If none of the conditions match
        else {
            showAlert("Login Failed", "Incorrect username or password!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
