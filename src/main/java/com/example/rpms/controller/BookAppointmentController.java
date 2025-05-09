package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.rpms.model.DatabaseConnector;

public class BookAppointmentController {
    @FXML private ComboBox<String> doctorComboBox;
    @FXML private DatePicker appointmentDate;
    @FXML private ComboBox<String> timeSlotComboBox;
    @FXML private TextArea reasonTextArea;
    @FXML private Label statusLabel;

    private String patientId;  // Changed from int to String

    @FXML
    public void initialize() {
        // Set default values and initialize components
        appointmentDate.setValue(LocalDate.now());
        loadDoctors();
        setupTimeSlots();
        
        // Add listener to doctor selection to update available time slots
        doctorComboBox.setOnAction(e -> updateAvailableTimeSlots());
    }

    public void setPatientId(String patientId) {  // Changed from int to String
        this.patientId = patientId;
    }

    private void loadDoctors() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT username FROM users WHERE role = 'DOCTOR'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                ObservableList<String> doctors = FXCollections.observableArrayList();
                while (rs.next()) {
                    doctors.add(rs.getString("username"));
                }
                doctorComboBox.setItems(doctors);
            }
        } catch (SQLException e) {
            showError("Error loading doctors: " + e.getMessage());
        }
    }

    private void setupTimeSlots() {
        ObservableList<String> timeSlots = FXCollections.observableArrayList(
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM",
            "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM",
            "04:00 PM", "04:30 PM", "05:00 PM"
        );
        timeSlotComboBox.setItems(timeSlots);
    }

    private void updateAvailableTimeSlots() {
        if (doctorComboBox.getValue() == null || appointmentDate.getValue() == null) {
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT appointment_time FROM appointments " +
                        "WHERE doctor_id = (SELECT user_id FROM users WHERE username = ?) " +
                        "AND appointment_date = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, doctorComboBox.getValue());
                stmt.setDate(2, java.sql.Date.valueOf(appointmentDate.getValue()));
                
                ResultSet rs = stmt.executeQuery();
                ObservableList<String> bookedSlots = FXCollections.observableArrayList();
                while (rs.next()) {
                    bookedSlots.add(rs.getString("appointment_time"));
                }
                
                // Remove booked slots from available slots
                timeSlotComboBox.getItems().removeAll(bookedSlots);
            }
        } catch (SQLException e) {
            showError("Error updating time slots: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookAppointment() {
        if (!validateInputs()) {
            return;
        }

        saveAppointment();
    }

    private void saveAppointment() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, " +
                        "appointment_time, reason, status) VALUES (?, " +
                        "(SELECT user_id FROM users WHERE username = ?), ?, ?, ?, 'PENDING')";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patientId);  // Changed from setInt to setString
                stmt.setString(2, doctorComboBox.getValue());
                stmt.setDate(3, java.sql.Date.valueOf(appointmentDate.getValue()));
                stmt.setString(4, timeSlotComboBox.getValue());
                stmt.setString(5, reasonTextArea.getText());
                
                int affected = stmt.executeUpdate();
                if (affected > 0) {
                    showSuccess("Appointment booked successfully!");
                    clearFields();
                }
            }
        } catch (SQLException e) {
            showError("Error booking appointment: " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (doctorComboBox.getValue() == null) {
            showError("Please select a doctor");
            return false;
        }
        if (appointmentDate.getValue() == null) {
            showError("Please select a date");
            return false;
        }
        if (appointmentDate.getValue().isBefore(LocalDate.now())) {
            showError("Please select a future date");
            return false;
        }
        if (timeSlotComboBox.getValue() == null) {
            showError("Please select a time slot");
            return false;
        }
        if (reasonTextArea.getText().trim().isEmpty()) {
            showError("Please provide a reason for the appointment");
            return false;
        }
        return true;
    }

    private void clearFields() {
        doctorComboBox.setValue(null);
        appointmentDate.setValue(LocalDate.now());
        timeSlotComboBox.setValue(null);
        reasonTextArea.clear();
    }

    public void cleanup() {
        // Add any cleanup code here if needed
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: green;");
    }
}
