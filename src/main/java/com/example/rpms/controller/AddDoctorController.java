package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import com.example.rpms.model.DatabaseConnector;

public class AddDoctorController {

    @FXML private TextField nameField;
    @FXML private TextField specializationField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneNumberField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dobPicker;
    @FXML private TextArea addressArea;
    @FXML private TextField qualificationField;
    @FXML private TextField experienceField;
    @FXML private TextField timeSlotField;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // Add debug logging
        System.out.println("Initializing AddDoctorController");
        System.out.println("PasswordField: " + (passwordField != null ? "found" : "null"));
        
        genderComboBox.setItems(FXCollections.observableArrayList(
            "Male", "Female", "Other"
        ));
    }

    @FXML
    private void handleAddDoctor() {
        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int userId = insertUser(conn);
                if (userId > 0) {
                    if (insertDoctorDetails(conn, userId)) {
                        conn.commit();
                        showSuccess("Doctor successfully registered!");
                        clearFields();
                    } else {
                        conn.rollback();
                        showError("Failed to add doctor details.");
                    }
                } else {
                    conn.rollback();
                    showError("Failed to create user account.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private int insertUser(Connection conn) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'DOCTOR')";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nameField.getText().trim());
            stmt.setString(2, passwordField.getText());
            stmt.setString(3, emailField.getText().trim());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private boolean insertDoctorDetails(Connection conn, int doctorId) throws SQLException {
        String sql = "INSERT INTO doctor_details (doctor_id, specialization, phone_number, gender, " +
                    "date_of_birth, address, qualification, years_of_experience, available_time_slot) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, specializationField.getText().trim());
            stmt.setString(3, phoneNumberField.getText().trim());
            stmt.setString(4, genderComboBox.getValue());
            stmt.setDate(5, dobPicker.getValue() != null ? java.sql.Date.valueOf(dobPicker.getValue()) : null);
            stmt.setString(6, addressArea.getText().trim());
            stmt.setString(7, qualificationField.getText().trim());
            stmt.setInt(8, Integer.parseInt(experienceField.getText().trim()));
            stmt.setString(9, timeSlotField.getText().trim());
            
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        // Check if fields are properly initialized
        if (nameField == null || emailField == null || passwordField == null || 
            specializationField == null || phoneNumberField == null || genderComboBox == null || 
            dobPicker == null || addressArea == null || qualificationField == null || 
            experienceField == null || timeSlotField == null) {
            showError("UI Components not properly initialized");
            return false;
        }

        if (nameField.getText().trim().isEmpty()) errors.append("Name is required\n");
        if (emailField.getText().trim().isEmpty()) {
            errors.append("Email is required\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("Invalid email format\n");
        }
        if (passwordField.getText().isEmpty()) {
            errors.append("Password is required\n");
        } else if (passwordField.getText().length() < 6) {
            errors.append("Password must be at least 6 characters\n");
        }
        if (specializationField.getText().trim().isEmpty()) errors.append("Specialization is required\n");
        if (phoneNumberField.getText().trim().isEmpty()) {
            errors.append("Phone number is required\n");
        } else if (!phoneNumberField.getText().matches("\\d{10,11}")) {
            errors.append("Invalid phone number format\n");
        }
        if (genderComboBox.getValue() == null) errors.append("Gender is required\n");
        if (dobPicker.getValue() == null) {
            errors.append("Date of birth is required\n");
        } else if (dobPicker.getValue().isAfter(LocalDate.now())) {
            errors.append("Invalid date of birth\n");
        }
        if (qualificationField.getText().trim().isEmpty()) errors.append("Qualification is required\n");
        if (experienceField.getText().trim().isEmpty()) {
            errors.append("Years of experience is required\n");
        } else {
            try {
                int exp = Integer.parseInt(experienceField.getText().trim());
                if (exp < 0) errors.append("Invalid years of experience\n");
            } catch (NumberFormatException e) {
                errors.append("Years of experience must be a number\n");
            }
        }
        if (timeSlotField.getText().trim().isEmpty()) errors.append("Time slot is required\n");

        if (errors.length() > 0) {
            showError(errors.toString());
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71;");
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        specializationField.clear();
        phoneNumberField.clear();
        genderComboBox.setValue(null);
        dobPicker.setValue(null);
        addressArea.clear();
        qualificationField.clear();
        experienceField.clear();
        timeSlotField.clear();
        statusLabel.setText("");
    }
}
