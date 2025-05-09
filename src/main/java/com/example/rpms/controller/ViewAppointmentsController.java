package com.example.rpms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import com.example.rpms.model.DatabaseConnector;

public class ViewAppointmentsController {

    @FXML private TableView<Object[]> appointmentTable;
    @FXML private TableColumn<Object[], Integer> idColumn;
    @FXML private TableColumn<Object[], String> patientNameColumn;
    @FXML private TableColumn<Object[], String> doctorNameColumn;
    @FXML private TableColumn<Object[], String> dateColumn;
    @FXML private TableColumn<Object[], String> timeColumn;
    @FXML private TableColumn<Object[], String> statusColumn;
    @FXML private Label statusLabel;

    private ObservableList<Object[]> appointmentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty((Integer) data.getValue()[0]).asObject());
        patientNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue()[1]));
        doctorNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue()[2]));
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue()[3]));
        timeColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue()[4]));
        statusColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty((String) data.getValue()[5]));

        loadAppointments();
    }

    @FXML
    public void handleRefresh() {
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentList.clear();
        String sql = "SELECT a.appointment_id, p.username as patient_name, d.username as doctor_name, " +
                    "DATE_FORMAT(a.appointment_date, '%Y-%m-%d') as app_date, " +
                    "DATE_FORMAT(a.appointment_date, '%h:%i %p') as app_time, " +
                    "a.status " +
                    "FROM appointments a " +
                    "JOIN users p ON a.patient_id = p.user_id " +
                    "JOIN users d ON a.doctor_id = d.user_id " +
                    "ORDER BY a.appointment_date DESC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("appointment_id"),
                    rs.getString("patient_name"),
                    rs.getString("doctor_name"),
                    rs.getString("app_date"),
                    rs.getString("app_time"),
                    rs.getString("status")
                };
                appointmentList.add(row);
            }
            appointmentTable.setItems(appointmentList);
            showSuccess("Appointments loaded successfully");
        } catch (SQLException e) {
            showError("Error loading appointments: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
    }
}
