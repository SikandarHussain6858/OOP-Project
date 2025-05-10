package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import com.example.rpms.model.DatabaseConnector;

public class BookAppointmentController {
    @FXML private ComboBox<String> patientComboBox;
    @FXML private ComboBox<String> doctorComboBox;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private TextField timeField;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;
    
    private String patientId;  // Store the patient ID

    @FXML
    public void initialize() {
        // Only load doctors if no patient ID is set (admin mode)
        if (patientId == null) {
            loadPatients();
        }
        loadDoctors();
        
        // Set up date picker to only allow future dates
        appointmentDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

    private void loadPatients() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT username FROM users WHERE role = 'PATIENT'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    patientComboBox.getItems().add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }

    private void loadDoctors() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT username FROM users WHERE role = 'DOCTOR'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    doctorComboBox.getItems().add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            showError("Error loading doctors: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookAppointment() {
        if (!validateInputs()) {
            return;
        }

        String selectedPatient = patientComboBox.getValue();
        String selectedDoctor = doctorComboBox.getValue();
        LocalDate appointmentDate = appointmentDatePicker.getValue();
        String appointmentTime = timeField.getText();
        String notes = notesArea.getText();

        try (Connection conn = DatabaseConnector.getConnection()) {
            // First get the patient ID if it's not already set
            if (patientId == null) {
                String patientSql = "SELECT user_id FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(patientSql)) {
                    stmt.setString(1, selectedPatient);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        patientId = rs.getString("user_id");
                    } else {
                        showError("Selected patient not found.");
                        return;
                    }
                }
            }

            // Get the doctor ID
            String doctorId = null;
            String doctorSql = "SELECT user_id FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(doctorSql)) {
                stmt.setString(1, selectedDoctor);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    doctorId = rs.getString("user_id");
                } else {
                    showError("Selected doctor not found.");
                    return;
                }
            }

            // Parse the time and combine with date
            LocalTime time = LocalTime.parse(appointmentTime);
            LocalDateTime appointmentDateTime = appointmentDate.atTime(time);

            // Check doctor availability
            if (!isDoctorAvailable(conn, doctorId, appointmentDateTime)) {
                showError("Doctor is not available at the selected time.");
                return;
            }

            // Insert the appointment
            String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, notes, status) VALUES (?, ?, ?, ?, 'SCHEDULED')";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);
                stmt.setString(2, doctorId);
                stmt.setTimestamp(3, Timestamp.valueOf(appointmentDateTime));
                stmt.setString(4, notes);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    showSuccess("Appointment booked successfully!");
                    clearFields();
                } else {
                    showError("Failed to book appointment.");
                }
            }
        } catch (SQLException e) {
            showError("Error booking appointment: " + e.getMessage());
        } catch (DateTimeParseException e) {
            showError("Invalid time format. Please use HH:mm format (e.g., 14:30)");
        }
    }

    private boolean validateInputs() {
        if ((patientId == null && patientComboBox.getValue() == null) || 
            doctorComboBox.getValue() == null || 
            appointmentDatePicker.getValue() == null || 
            timeField.getText().isEmpty()) {
            showError("Please fill in all required fields.");
            return false;
        }
        
        // Validate time format (HH:mm)
        if (!timeField.getText().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showError("Please enter a valid time in HH:mm format.");
            return false;
        }
        
        return true;
    }

    private boolean isDoctorAvailable(Connection conn, String doctorId, LocalDateTime dateTime) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND status != 'CANCELLED'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctorId);
            stmt.setTimestamp(2, Timestamp.valueOf(dateTime));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return false;
    }

    private void clearFields() {
        if (patientId == null) {
            patientComboBox.setValue(null);
        }
        doctorComboBox.setValue(null);
        appointmentDatePicker.setValue(null);
        timeField.clear();
        notesArea.clear();
        statusLabel.setText("");
    }

    private void showError(String message) {
        statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        statusLabel.setText(message);
    }

    private void showSuccess(String message) {
        statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
        statusLabel.setText(message);
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
        // When patient ID is set, load their username and set it in the combo box
        if (patientId != null) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                String sql = "SELECT username FROM users WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, patientId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String patientUsername = rs.getString("username");
                        patientComboBox.setValue(patientUsername);
                        patientComboBox.setDisable(true); // Lock the patient selection
                    }
                }
            } catch (SQLException e) {
                showError("Error setting patient: " + e.getMessage());
            }
        }
    }
}
