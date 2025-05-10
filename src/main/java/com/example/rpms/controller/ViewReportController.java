package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.example.rpms.model.DatabaseConnector;
import javafx.scene.text.Text;
import java.sql.SQLException;

public class ViewReportController {
    private String patientId;
    
    @FXML
    private TableView<Report> reportsTable;
    @FXML private Text patientInfoText;
    @FXML private Text vitalsText;
    @FXML private Text feedbackText;
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
        loadPatientInfo();
        loadReports();
        loadLatestVitals();
        loadLatestFeedback();
    }
    
    @FXML
    public void initialize() {
        // Initialize table columns
        setupTableColumns();
    }
    
    @SuppressWarnings("unchecked")
    private void setupTableColumns() {
        TableColumn<Report, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> data.getValue().dateProperty());
        TableColumn<Report, String> typeColumn = new TableColumn<>("Report Type");
        typeColumn.setCellValueFactory(data -> data.getValue().typeProperty());
        TableColumn<Report, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        reportsTable.getColumns().setAll(dateColumn, typeColumn, descriptionColumn);
    }
    
    private void loadReports() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT date, report_type, description FROM reports WHERE patient_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            ObservableList<Report> reports = FXCollections.observableArrayList();
            while (rs.next()) {
                reports.add(new Report(
                    rs.getString("date"),
                    rs.getString("report_type"),
                    rs.getString("description")
                ));
            }
            
            reportsTable.setItems(reports);
        } catch (Exception e) {
            e.printStackTrace();
            // Show error alert
        }
    }
    
    private void loadPatientInfo() {
        try (Connection conn = com.example.rpms.model.DatabaseConnector.getConnection()) {
            String sql = "SELECT u.username, u.email, pd.dob, pd.gender, pd.contact, pd.address FROM users u JOIN patient_details pd ON u.user_id = pd.patient_id WHERE u.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String info = String.format("Name: %s\nEmail: %s\nDOB: %s\nGender: %s\nContact: %s\nAddress: %s",
                        rs.getString("username"), rs.getString("email"), rs.getString("dob"), rs.getString("gender"), rs.getString("contact"), rs.getString("address"));
                patientInfoText.setText(info);
                patientInfoText.setOpacity(1.0);
            } else {
                patientInfoText.setText("No patient info found.");
                patientInfoText.setOpacity(0.5);
            }
        } catch (SQLException e) {
            patientInfoText.setText("Error loading patient info: " + e.getMessage());
            patientInfoText.setOpacity(0.5);
        }
    }

    private void loadLatestVitals() {
        try (Connection conn = com.example.rpms.model.DatabaseConnector.getConnection()) {
            String sql = "SELECT heart_rate, oxygen_saturation, bp_systolic, bp_diastolic, temperature, recorded_at FROM patient_vitals WHERE patient_id = ? ORDER BY recorded_at DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String vitals = String.format("Heart Rate: %s bpm\nO2 Saturation: %s%%\nBP: %s/%s mmHg\nTemp: %s°C\nRecorded: %s",
                        rs.getString("heart_rate"), rs.getString("oxygen_saturation"), rs.getString("bp_systolic"), rs.getString("bp_diastolic"), rs.getString("temperature"), rs.getString("recorded_at"));
                vitalsText.setText(vitals);
                vitalsText.setOpacity(1.0);
            } else {
                vitalsText.setText("No vitals found.");
                vitalsText.setOpacity(0.5);
            }
        } catch (SQLException e) {
            vitalsText.setText("Error loading vitals: " + e.getMessage());
            vitalsText.setOpacity(0.5);
        }
    }

    private void loadLatestFeedback() {
        try (Connection conn = com.example.rpms.model.DatabaseConnector.getConnection()) {
            String sql = "SELECT f.feedback, f.date, u.username as doctor_name FROM feedbacks f JOIN users u ON f.doctor_id = u.user_id WHERE f.patient_id = ? ORDER BY f.date DESC LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String feedback = String.format("Doctor: %s\nDate: %s\nFeedback: %s",
                        rs.getString("doctor_name"), rs.getString("date"), rs.getString("feedback"));
                feedbackText.setText(feedback);
                feedbackText.setOpacity(1.0);
            } else {
                feedbackText.setText("No feedback found.");
                feedbackText.setOpacity(0.5);
            }
        } catch (SQLException e) {
            feedbackText.setText("Error loading feedback: " + e.getMessage());
            feedbackText.setOpacity(0.5);
        }
    }
    
    // Inner class to represent a report
    public static class Report {
        private final javafx.beans.property.SimpleStringProperty date;
        private final javafx.beans.property.SimpleStringProperty type;
        private final javafx.beans.property.SimpleStringProperty description;
        
        public Report(String date, String type, String description) {
            this.date = new javafx.beans.property.SimpleStringProperty(date);
            this.type = new javafx.beans.property.SimpleStringProperty(type);
            this.description = new javafx.beans.property.SimpleStringProperty(description);
        }
        
        public javafx.beans.property.StringProperty dateProperty() { return date; }
        public javafx.beans.property.StringProperty typeProperty() { return type; }
        public javafx.beans.property.StringProperty descriptionProperty() { return description; }
    }
}
