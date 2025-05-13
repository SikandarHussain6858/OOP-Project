package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.example.rpms.model.DatabaseConnector;
import javafx.scene.text.Text;
import java.sql.SQLException;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewReportController {
    private String patientId;
    
    @FXML
    private TableView<Report> reportsTable;
    @FXML private Text patientInfoText;
    @FXML private Text vitalsText;
    @FXML private Text feedbackText;
    
    // Table columns
    @FXML private TableColumn<Report, String> dateColumn;
    @FXML private TableColumn<Report, String> bloodPressureColumn;
    @FXML private TableColumn<Report, String> heartRateColumn;
    @FXML private TableColumn<Report, String> temperatureColumn;
    @FXML private TableColumn<Report, String> notesColumn;
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
        loadPatientInfo();
        loadReports();
        loadLatestVitals();
        loadLatestFeedback();
    }
    
    @FXML
    public void initialize() {
        // Setup table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        bloodPressureColumn.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));
        heartRateColumn.setCellValueFactory(new PropertyValueFactory<>("heartRate"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        
        // Add column labels
        dateColumn.setText("Date & Time");
        bloodPressureColumn.setText("Blood Pressure (mmHg)");
        heartRateColumn.setText("Heart Rate (bpm)");
        temperatureColumn.setText("Temperature (°C)");
        notesColumn.setText("Notes");
        
        // Set column widths
        dateColumn.setPrefWidth(150);
        bloodPressureColumn.setPrefWidth(150);
        heartRateColumn.setPrefWidth(120);
        temperatureColumn.setPrefWidth(120);
        notesColumn.setPrefWidth(200);
        
        // Enable column resize policy
        reportsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void loadReports() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT pv.*, DATE_FORMAT(pv.recorded_at, '%Y-%m-%d %H:%i') as formatted_date 
                FROM patient_vitals pv 
                WHERE pv.patient_id = ? 
                ORDER BY pv.recorded_at DESC
                """;
                
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            ObservableList<Report> reports = FXCollections.observableArrayList();
            while (rs.next()) {
                reports.add(new Report(
                    rs.getString("formatted_date"),
                    String.format("%d/%d", rs.getInt("bp_systolic"), rs.getInt("bp_diastolic")),
                    String.valueOf(rs.getInt("heart_rate")),
                    String.format("%.1f", rs.getDouble("temperature")),
                    rs.getString("notes")
                ));
            }
            
            reportsTable.setItems(reports);
            
            if (reports.isEmpty()) {
                feedbackText.setText("No vitals records found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            feedbackText.setText("Error loading reports: " + e.getMessage());
        }
    }
    
    private void loadPatientInfo() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT u.username, u.email, pd.dob, pd.gender, pd.contact, pd.address 
                FROM users u 
                JOIN patient_details pd ON u.user_id = pd.patient_id 
                WHERE u.user_id = ?
                """;
                
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                StringBuilder info = new StringBuilder();
                info.append("👤 Patient Information:\n\n");
                info.append(String.format("Name: %s\n", rs.getString("username")));
                info.append(String.format("Email: %s\n", rs.getString("email")));
                info.append(String.format("Date of Birth: %s\n", rs.getString("dob")));
                info.append(String.format("Gender: %s\n", rs.getString("gender")));
                info.append(String.format("Contact: %s\n", rs.getString("contact")));
                info.append(String.format("Address: %s", rs.getString("address")));
                
                patientInfoText.setText(info.toString());
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
        private final StringProperty date;
        private final StringProperty bloodPressure;
        private final StringProperty heartRate;
        private final StringProperty temperature;
        private final StringProperty notes;

        public Report(String date, String bloodPressure, String heartRate, String temperature, String notes) {
            this.date = new SimpleStringProperty(date);
            this.bloodPressure = new SimpleStringProperty(bloodPressure);
            this.heartRate = new SimpleStringProperty(heartRate);
            this.temperature = new SimpleStringProperty(temperature);
            this.notes = new SimpleStringProperty(notes);
        }

        // Getters for JavaFX properties
        public StringProperty dateProperty() { return date; }
        public StringProperty bloodPressureProperty() { return bloodPressure; }
        public StringProperty heartRateProperty() { return heartRate; }
        public StringProperty temperatureProperty() { return temperature; }
        public StringProperty notesProperty() { return notes; }
    }
}
