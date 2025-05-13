package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import com.example.rpms.model.DatabaseConnector;
import javafx.stage.Stage;

public class AdminDoctorEmergencyController {
    
    @FXML private TableView<EmergencyAlert> emergencyTable;
    @FXML private TableColumn<EmergencyAlert, String> dateColumn;
    @FXML private TableColumn<EmergencyAlert, String> patientColumn;
    @FXML private TableColumn<EmergencyAlert, String> messageColumn;
    @FXML private TableColumn<EmergencyAlert, String> statusColumn;
    @FXML private TableColumn<EmergencyAlert, String> severityColumn;
    @FXML private Label statusLabel;
    
    private String userId;
    private String userRole;
    
    public class EmergencyAlert {
        private String id;
        private String date;
        private String patient;
        private String message;
        private String status;
        private String severity;
        
        // Constructor
        public EmergencyAlert(String id, String date, String patient, String message, String status, String severity) {
            this.id = id;
            this.date = date;
            this.patient = patient;
            this.message = message;
            this.status = status;
            this.severity = severity;
        }
     private String doctorId;

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        // Add logic to handle doctorId if needed
    }
        // Getters
        public String getId() { return id; }
        public String getDate() { return date; }
        public String getPatient() { return patient; }
        public String getMessage() { return message; }
        public String getStatus() { return status; }
        public String getSeverity() { return severity; }
    }
    
    @FXML
    public void initialize() {
        setupTableColumns();
    }
    
    private void setupTableColumns() {
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate()));
        patientColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPatient()));
        messageColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMessage()));
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
            severityColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getSeverity()));
    }
    
    public void setUserInfo(String userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
        loadEmergencyAlerts();
    }
    
    private void loadEmergencyAlerts() {
        ObservableList<EmergencyAlert> alerts = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT ea.*, u.username as patient_name, 
                       COALESCE(u2.username, '') as resolver_name
                FROM emergency_alerts ea
                JOIN users u ON ea.patient_id = u.user_id
                LEFT JOIN users u2 ON ea.resolved_by = u2.user_id
                ORDER BY ea.created_at DESC
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    alerts.add(new EmergencyAlert(
                        String.valueOf(rs.getInt("id")),
                        rs.getTimestamp("created_at").toString(),
                        rs.getString("patient_name"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getString("type")  // Changed from severity to type
                    ));
                }
            }
            
            emergencyTable.setItems(alerts);
            
            if (alerts.isEmpty()) {
                statusLabel.setTextFill(Color.BLUE);
                statusLabel.setText("No emergency alerts found");
            } else {
                statusLabel.setText("");
            }
            
        } catch (SQLException e) {
            e.printStackTrace(); // For debugging
            showError("Error loading emergency alerts: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleResolveAlert() {
        EmergencyAlert selectedAlert = emergencyTable.getSelectionModel().getSelectedItem();
        if (selectedAlert == null) {
            showError("Please select an alert to resolve");
            return;
        }
        
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "UPDATE emergency_alerts SET status = 'RESOLVED', resolved_by = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userId);
                stmt.setString(2, selectedAlert.getId());
                
                int affected = stmt.executeUpdate();
                if (affected > 0) {
                    showSuccess("Alert marked as resolved");
                    loadEmergencyAlerts(); // Refresh the table
                }
            }
        } catch (SQLException e) {
            showError("Error resolving alert: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleContactPatient() {
        EmergencyAlert selectedAlert = emergencyTable.getSelectionModel().getSelectedItem();
        if (selectedAlert == null) {
            showError("Please select an alert to contact patient");
            return;
        }
        
        // Implementation for contacting patient (e.g., opening email/messaging interface)
        showSuccess("Opening messaging interface...");
    }
    
    private void showError(String message) {
        statusLabel.setTextFill(Color.RED);
        statusLabel.setText(message);
    }
    
    private void showSuccess(String message) {
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText(message);
    }

    public void setDoctorId(String doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDoctorId'");
    }
}