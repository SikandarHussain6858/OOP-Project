package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class ViewPrescriptionsController {
    @FXML private TableView<Object[]> prescriptionsTable;
    @FXML private TableColumn<Object[], String> dateColumn;
    @FXML private TableColumn<Object[], String> doctorColumn;
    @FXML private TableColumn<Object[], String> diagnosisColumn;
    @FXML private TableColumn<Object[], String> medicationsColumn;
    @FXML private TableColumn<Object[], String> instructionsColumn;
    @FXML private Label statusLabel;

    private String patientId;
    private ObservableList<Object[]> prescriptionsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
    }

    private void setupColumns() {
        dateColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[0]));
        doctorColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[1]));
        diagnosisColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[2]));
        medicationsColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[3]));
        instructionsColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[4]));
    }

    public void setPatientId(String id) {
        this.patientId = id;
        loadPrescriptions();
    }

    private void loadPrescriptions() {
        prescriptionsList.clear();
        String sql = """
            SELECT DATE_FORMAT(p.created_at, '%Y-%m-%d') as date,
                   u.username as doctor_name,
                   p.diagnosis, p.medications, p.instructions
            FROM prescriptions p
            JOIN users u ON p.doctor_id = u.user_id
            WHERE p.patient_id = ?
            ORDER BY p.created_at DESC
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("date"),
                    rs.getString("doctor_name"),
                    rs.getString("diagnosis"),
                    rs.getString("medications"),
                    rs.getString("instructions")
                };
                prescriptionsList.add(row);
            }
            prescriptionsTable.setItems(prescriptionsList);
            
            if (prescriptionsList.isEmpty()) {
                showInfo("No prescriptions found");
            } else {
                showSuccess("Prescriptions loaded successfully");
            }
        } catch (SQLException e) {
            showError("Error loading prescriptions: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadPrescriptions();
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71;");
    }

    private void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setStyle("-fx-text-fill: #3498db;");
    }
}
