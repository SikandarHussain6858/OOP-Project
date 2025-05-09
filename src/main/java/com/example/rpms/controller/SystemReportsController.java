package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import com.example.rpms.model.DatabaseConnector;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class SystemReportsController {
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private ListView<String> patientStatsList;
    @FXML private ListView<String> doctorStatsList;
    @FXML private ListView<String> appointmentStatsList;

    @FXML
    public void initialize() {
        startDate.setValue(LocalDate.now().minusMonths(1));
        endDate.setValue(LocalDate.now());
        loadAllStats();
    }

    @FXML
    private void handleFilterDates() {
        loadAllStats();
    }

    private void loadAllStats() {
        loadPatientStats();
        loadDoctorStats();
        loadAppointmentStats();
    }

    private void loadPatientStats() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            List<String> stats = new ArrayList<>();
            
            // Total patients
            String sql = "SELECT COUNT(*) FROM users WHERE role = 'PATIENT'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.add("Total Patients: " + rs.getInt(1));
                }
            }

            // New patients in date range
            sql = "SELECT COUNT(*) FROM users WHERE role = 'PATIENT' AND created_at BETWEEN ? AND ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(startDate.getValue()));
                stmt.setDate(2, java.sql.Date.valueOf(endDate.getValue()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.add("New Patients in Period: " + rs.getInt(1));
                }
            }

            patientStatsList.getItems().setAll(stats);
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void loadDoctorStats() {
        // Similar implementation for doctor statistics
    }

    private void loadAppointmentStats() {
        // Similar implementation for appointment statistics
    }

    @FXML
    private void handleExportPatientReport() {
        exportReport("patient_report", patientStatsList.getItems());
    }

    @FXML
    private void handleExportDoctorReport() {
        exportReport("doctor_report", doctorStatsList.getItems());
    }

    @FXML
    private void handleExportAppointmentReport() {
        exportReport("appointment_report", appointmentStatsList.getItems());
    }

    private void exportReport(String prefix, List<String> items) {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDate.now());
        String filename = prefix + "_" + timestamp + ".csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Report Generated on: " + LocalDate.now());
            writer.println("Period: " + startDate.getValue() + " to " + endDate.getValue());
            writer.println();
            
            for (String item : items) {
                writer.println(item);
            }
            
            showSuccess("Report exported to: " + filename);
        } catch (Exception e) {
            showError("Failed to export report: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
}