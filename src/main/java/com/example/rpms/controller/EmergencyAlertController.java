package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDateTime;
import javafx.scene.control.cell.PropertyValueFactory;

public class EmergencyAlertController {
    @FXML private ComboBox<String> alertTypeComboBox;
    @FXML private TextArea messageArea;
    @FXML private TableView<EmergencyAlert> alertsTable;
    @FXML private TableColumn<EmergencyAlert, LocalDateTime> dateColumn;
    @FXML private TableColumn<EmergencyAlert, String> typeColumn;
    @FXML private TableColumn<EmergencyAlert, String> messageColumn;
    @FXML private TableColumn<EmergencyAlert, String> statusColumn;

    private String patientId;
    private final ObservableList<EmergencyAlert> alerts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize alert types
        alertTypeComboBox.getItems().addAll(
            "Medical Emergency",
            "Medication Issue",
            "Urgent Consultation",
            "Other Emergency"
        );

        // Initialize table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("alertType"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        alertsTable.setItems(alerts);
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
        loadAlerts();
    }

    private void loadAlerts() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT * FROM emergency_alerts WHERE patient_id = ? ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            alerts.clear();
            while (rs.next()) {
                alerts.add(new EmergencyAlert(
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("alert_type"),
                    rs.getString("alert_message"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading alerts: " + e.getMessage());
        }
    }    @FXML
    private void handleSubmitAlert() {
        String alertType = alertTypeComboBox.getValue();
        String message = messageArea.getText().trim();

        if (alertType == null || alertType.isEmpty()) {
            showError("Please select an alert type");
            return;
        }

        if (message.isEmpty()) {
            showError("Please enter an alert message");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Create the alert
                String sql = "INSERT INTO emergency_alerts (patient_id, alert_type, alert_message, status, created_at) VALUES (?, ?, ?, 'ACTIVE', CURRENT_TIMESTAMP)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, patientId);
                stmt.setString(2, alertType);
                stmt.setString(3, message);
                stmt.executeUpdate();

                // Send notifications to associated doctors
                String notificationSql = "INSERT INTO notifications (user_id, message, created_at) " +
                      "SELECT pd.doctor_id, CONCAT('New emergency alert from ', p.username, ': ', ?) as message, CURRENT_TIMESTAMP " +
                      "FROM patient_doctor pd " +
                      "JOIN users p ON pd.patient_id = p.user_id " +
                      "WHERE pd.patient_id = ?";
                PreparedStatement notifyStmt = conn.prepareStatement(notificationSql);
                notifyStmt.setString(1, alertType + " - " + message);
                notifyStmt.setString(2, patientId);
                notifyStmt.executeUpdate();

                conn.commit();

                // Clear form
                alertTypeComboBox.setValue(null);
                messageArea.clear();
                
                // Reload alerts to show the new one
                loadAlerts();
                
                showInfo("Emergency alert submitted successfully");
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        showError("Error rolling back transaction: " + ex.getMessage());
                    }
                }
                showError("Error submitting alert: " + e.getMessage());
            }
        } catch (SQLException e) {
            showError("Error submitting alert: " + e.getMessage());
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
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class EmergencyAlert {
        private final LocalDateTime createdAt;
        private final String alertType;
        private final String message;
        private final String status;

        public EmergencyAlert(LocalDateTime createdAt, String alertType, String message, String status) {
            this.createdAt = createdAt;
            this.alertType = alertType;
            this.message = message;
            this.status = status;
        }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public String getAlertType() { return alertType; }
        public String getMessage() { return message; }
        public String getStatus() { return status; }
    }

    public void loadActiveAlerts() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT * FROM emergency_alerts WHERE status = 'ACTIVE' ORDER BY created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            alerts.clear();
            while (rs.next()) {
                alerts.add(new EmergencyAlert(
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("alert_type"),
                    rs.getString("alert_message"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading active alerts: " + e.getMessage());
        }
    }
}
