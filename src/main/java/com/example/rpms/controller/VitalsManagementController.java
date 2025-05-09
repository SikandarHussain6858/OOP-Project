package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.example.rpms.model.DatabaseConnector;
import com.example.rpms.model.Vitals; // Ensure this is the correct package for VitalsRecord
import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;

public class VitalsManagementController {
    @FXML private TextField bpSystolicField;
    @FXML private TextField bpDiastolicField;
    @FXML private TextField heartRateField;
    @FXML private TextField temperatureField;
    @FXML private TextField oxygenField;
    @FXML private TextField glucoseField;
    @FXML private Label selectedFileLabel;
    @FXML private TableView<Vitals> vitalsTable;
    
    private String patientId;
    private ObservableList<Vitals> vitalsList = FXCollections.observableArrayList();

    public void setPatientId(String id) {
        this.patientId = id;
        loadVitalsHistory();
    }

    @FXML
    private void handleSaveVitals() {
        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO patient_vitals (patient_id, bp_systolic, bp_diastolic, " +
                        "heart_rate, temperature, oxygen_saturation, glucose, recorded_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                stmt.setDouble(2, Double.parseDouble(bpSystolicField.getText().trim()));
                stmt.setDouble(3, Double.parseDouble(bpDiastolicField.getText().trim()));
                stmt.setDouble(4, Double.parseDouble(heartRateField.getText().trim()));
                stmt.setDouble(5, Double.parseDouble(temperatureField.getText().trim()));
                stmt.setDouble(6, Double.parseDouble(oxygenField.getText().trim()));
                stmt.setDouble(7, Double.parseDouble(glucoseField.getText().trim()));
                stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

                int affected = stmt.executeUpdate();
                if (affected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Vitals saved successfully");
                    clearForm();
                    loadVitalsHistory();
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save vitals: " + e.getMessage());
        }
    }

    @FXML
    private void handleChooseCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedFileLabel.setText(file.getName());
            handleCSVImport(file);
        }
    }

    private void handleCSVImport(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length >= 6) {
                    saveVitalsFromCSV(values);
                }
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "CSV imported successfully");
            loadVitalsHistory();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to read CSV: " + e.getMessage());
        }
    }

    private void saveVitalsFromCSV(String[] values) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO patient_vitals (patient_id, bp_systolic, bp_diastolic, " +
                        "heart_rate, temperature, oxygen_saturation, glucose, recorded_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                stmt.setDouble(2, Double.parseDouble(values[0].trim()));
                stmt.setDouble(3, Double.parseDouble(values[1].trim()));
                stmt.setDouble(4, Double.parseDouble(values[2].trim()));
                stmt.setDouble(5, Double.parseDouble(values[3].trim()));
                stmt.setDouble(6, Double.parseDouble(values[4].trim()));
                stmt.setDouble(7, Double.parseDouble(values[5].trim()));
                stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

                stmt.executeUpdate();
            }
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save vitals from CSV: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
    }

    private void loadVitalsHistory() {
        vitalsList.clear();
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT * FROM patient_vitals WHERE patient_id = ? ORDER BY recorded_at DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    vitalsList.add(new Vitals(
                        rs.getTimestamp("recorded_at").toLocalDateTime(),
                        rs.getDouble("bp_systolic"),
                        rs.getDouble("bp_diastolic"),
                        rs.getDouble("heart_rate"),
                        rs.getDouble("temperature"),
                        rs.getDouble("oxygen_saturation"),
                        rs.getDouble("glucose")
                    ));
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load vitals history: " + e.getMessage());
        }
        vitalsTable.setItems(vitalsList);
    }

    private boolean validateInputs() {
        try {
            Double.parseDouble(bpSystolicField.getText().trim());
            Double.parseDouble(bpDiastolicField.getText().trim());
            Double.parseDouble(heartRateField.getText().trim());
            Double.parseDouble(temperatureField.getText().trim());
            Double.parseDouble(oxygenField.getText().trim());
            Double.parseDouble(glucoseField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter valid numeric values in all fields.");
            return false;
        }
        return true;
    }
    

    private void clearForm() {
        bpSystolicField.clear();
        bpDiastolicField.clear();
        heartRateField.clear();
        temperatureField.clear();
        oxygenField.clear();
        glucoseField.clear();
    }
}