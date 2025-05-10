package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class DoctorEmergencyAlertController {
    @FXML private TableView<EmergencyAlert> alertsTable;
    @FXML private TableColumn<EmergencyAlert, LocalDateTime> dateColumn;
    @FXML private TableColumn<EmergencyAlert, String> patientColumn;
    @FXML private TableColumn<EmergencyAlert, String> typeColumn;
    @FXML private TableColumn<EmergencyAlert, String> messageColumn;
    @FXML private TableColumn<EmergencyAlert, String> statusColumn;

    private String doctorId;
    private final ObservableList<EmergencyAlert> alerts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("alertType"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add context menu for resolving alerts
        ContextMenu contextMenu = new ContextMenu();
        MenuItem resolveItem = new MenuItem("Mark as Resolved");
        resolveItem.setOnAction(e -> handleResolveAlert());
        contextMenu.getItems().add(resolveItem);
        
        alertsTable.setContextMenu(contextMenu);
        alertsTable.setItems(alerts);
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        loadActiveAlerts();
    }

    private void loadActiveAlerts() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT ea.*, u.username as patient_name, u.user_id as patient_id,
                       COALESCE(u.username, 'Unknown Patient') as display_name
                FROM emergency_alerts ea
                LEFT JOIN users u ON ea.patient_id = u.user_id
                JOIN patient_doctor pd ON ea.patient_id = pd.patient_id
                WHERE ea.status = 'ACTIVE' AND pd.doctor_id = ?
                ORDER BY ea.created_at DESC
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            alerts.clear();
            while (rs.next()) {
                alerts.add(new EmergencyAlert(
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("display_name"),
                    rs.getString("alert_type"),
                    rs.getString("alert_message"),
                    rs.getString("status"),
                    rs.getInt("alert_id"),
                    rs.getString("patient_id")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading alerts: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleContactPatient() {
        EmergencyAlert selectedAlert = alertsTable.getSelectionModel().getSelectedItem();
        if (selectedAlert == null) {
            showError("Please select an alert first");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/email.fxml"));
            Parent root = loader.load();
            
            EmailController controller = loader.getController();
            controller.setUserId(selectedAlert.getPatientId());
            controller.prepopulateEmergencyResponse(selectedAlert.getAlertType(), selectedAlert.getMessage());
            
            Stage stage = new Stage();
            stage.setTitle("Contact Patient");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Error opening messaging window: " + e.getMessage());
        }
    }

    @FXML
    private void handleResolveAlert() {
        EmergencyAlert selectedAlert = alertsTable.getSelectionModel().getSelectedItem();
        if (selectedAlert == null) {
            showError("Please select an alert to resolve");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "UPDATE emergency_alerts SET status = 'RESOLVED', resolved_at = NOW() WHERE alert_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedAlert.getAlertId());
            stmt.executeUpdate();

            // Reload the alerts
            loadActiveAlerts();
            showInfo("Alert marked as resolved");
        } catch (SQLException e) {
            showError("Error resolving alert: " + e.getMessage());
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
        private final String patientName;
        private final String alertType;
        private final String message;
        private final String status;
        private final int alertId;
        private final String patientId;

        public EmergencyAlert(LocalDateTime createdAt, String patientName, String alertType, 
                            String message, String status, int alertId, String patientId) {
            this.createdAt = createdAt;
            this.patientName = patientName;
            this.alertType = alertType;
            this.message = message;
            this.status = status;
            this.alertId = alertId;
            this.patientId = patientId;
        }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public String getPatientName() { return patientName; }
        public String getAlertType() { return alertType; }
        public String getMessage() { return message; }
        public String getStatus() { return status; }
        public int getAlertId() { return alertId; }
        public String getPatientId() { return patientId; }
    }
}
