package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class EmergencyAlertController {

    @FXML
    private ListView<String> alertsListView;

    @FXML
    private Button panicButton;

    @FXML
    private void handlePanicButtonAction() {
        String sql = "INSERT INTO emergency_alerts (patient_id, alert_type, alert_message) VALUES (?, 'PANIC', 'Emergency assistance needed')";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Assuming you have the current user's ID stored somewhere
            String currentPatientId = getCurrentPatientId();
            pstmt.setString(1, currentPatientId);
            pstmt.executeUpdate();

            // Show alert
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Emergency Alert");
            alert.setHeaderText("Emergency Triggered!");
            alert.setContentText("An emergency alert has been sent to your assigned doctor.");
            alert.showAndWait();

            // Send email notification
            notifyDoctor(currentPatientId);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to send emergency alert");
        }
    }

    private void notifyDoctor(String patientId) {
        // Implement email notification using your EmailNotification class
    }

    private String getCurrentPatientId() {
        // Implement method to get current patient ID
        return "1"; // Temporary return
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleSendAlert(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/email.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Send Email");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadActiveAlerts() {
        String sql = "SELECT ea.*, u.username FROM emergency_alerts ea " +
                "JOIN users u ON ea.patient_id = u.user_id " +
                "WHERE ea.status = 'ACTIVE' " +
                "ORDER BY ea.created_at DESC";

        ObservableList<String> alerts = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String alertInfo = String.format("Patient: %s\nType: %s\nMessage: %s\nTime: %s",
                        rs.getString("username"),
                        rs.getString("alert_type"),
                        rs.getString("alert_message"),
                        rs.getTimestamp("created_at").toLocalDateTime().format(formatter)
                );
                alerts.add(alertInfo);
            }

            if (alertsListView != null) {
                alertsListView.setItems(alerts);
            }

        } catch (SQLException e) {
            showError("Failed to load alerts: " + e.getMessage());
        }
    }
}
