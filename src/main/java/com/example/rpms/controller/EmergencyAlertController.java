package com.example.rpms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDateTime;

import com.example.rpms.model.DatabaseConnector;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class EmergencyAlertController {
    @FXML private ComboBox<String> alertTypeComboBox;
    @FXML private TextArea messageArea;
    @FXML private TableView<EmergencyAlert> alertsTable;
    @FXML private TableColumn<EmergencyAlert, LocalDateTime> dateColumn;
    @FXML private TableColumn<EmergencyAlert, String> typeColumn;
    @FXML private TableColumn<EmergencyAlert, String> messageColumn;
    @FXML private TableColumn<EmergencyAlert, String> statusColumn;
    @FXML private TableColumn<EmergencyAlert, String> patientNameColumn;
    @FXML private VBox submitAlertBox;
    @FXML private Label alertHistoryLabel;

    private String patientId;
    private String doctorId;
    private boolean isDoctorView = false;
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
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        alertsTable.setItems(alerts);
    }

    private void updateUIForRole() {
        if (isDoctorView) {
            submitAlertBox.setVisible(false);
            submitAlertBox.setManaged(false);
            alertHistoryLabel.setText("Patient Emergency Alerts");
            patientNameColumn.setVisible(true);
        } else {
            submitAlertBox.setVisible(true);
            submitAlertBox.setManaged(true);
            alertHistoryLabel.setText("Your Alert History");
            patientNameColumn.setVisible(false);
        }
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
        this.isDoctorView = false;
        updateUIForRole();
        loadAlerts();
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        this.isDoctorView = true;
        updateUIForRole();
        loadAlerts();
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Connection test only
        } catch (SQLException e) {
            showError("Error connecting to the database: " + e.getMessage());
        }
    }

    private void loadAlerts() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (isDoctorView) {
                // Load alerts for all patients assigned to this doctor
                sql = "SELECT ea.*, u.username as patient_name " +
                      "FROM emergency_alerts ea " +
                      "JOIN patient_doctor pd ON ea.patient_id = pd.patient_id " +
                      "JOIN users u ON ea.patient_id = u.user_id " +
                      "WHERE pd.doctor_id = ? " +
                      "ORDER BY ea.created_at DESC";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, doctorId);
            } else {
                // Load alerts for a specific patient
                sql = "SELECT ea.* " +
                      "FROM emergency_alerts ea " +
                      "WHERE ea.patient_id = ? " +
                      "ORDER BY ea.created_at DESC";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, patientId);
            }

            ResultSet rs = stmt.executeQuery();
            alerts.clear();
            while (rs.next()) {
                EmergencyAlert alert = new EmergencyAlert(
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("type"),
                    rs.getString("message"),
                    rs.getString("status")
                );
                if (isDoctorView) {
                    alert.setPatientName(rs.getString("patient_name"));
                }
                alerts.add(alert);
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
                // Updated column names to match database structure
                String sql = "INSERT INTO emergency_alerts (patient_id, type, message, status, created_at) " + 
                            "VALUES (?, ?, ?, 'ACTIVE', CURRENT_TIMESTAMP)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, patientId);
                stmt.setString(2, alertType);
                stmt.setString(3, message);
                stmt.executeUpdate();

                // Update notification query to use correct column names
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
        private String patientName; // Added for doctor view

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
        public String getPatientName() { return patientName; } // Getter for patient name
        public void setPatientName(String patientName) { this.patientName = patientName; } // Setter for patient name
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
