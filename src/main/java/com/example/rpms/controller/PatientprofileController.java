package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import com.example.rpms.model.Patient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;

public class PatientprofileController {
    // Existing labels
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label ageLabel;
    @FXML private Label genderLabel;
    @FXML private Label contactLabel;
    @FXML private Label addressLabel;
    @FXML private Label medicalHistoryLabel;
    @FXML private Label assignedDoctorLabel;
    @FXML private Label lastVisitLabel;

    // Edit fields
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderField;
    @FXML private TextField contactField;
    @FXML private TextField addressField;
    @FXML private TextArea medicalHistoryArea;
    @FXML private TextArea currentMedicationsArea;
    @FXML private Button editSaveButton;

    private boolean isEditMode = false;
    private Patient currentPatient;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Initialize gender options
        genderField.getItems().addAll("Male", "Female", "Other");

        // Initialize blood group options
    }

    public void loadProfile(Patient patient) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT p.*, u.name, u.email,
                    (SELECT d.name FROM doctors d 
                     JOIN patient_doctor pd ON d.doctor_id = pd.doctor_id 
                     WHERE pd.patient_id = p.patient_id LIMIT 1) as doctor_name,
                    (SELECT MAX(appointment_date) FROM appointments 
                     WHERE patient_id = p.patient_id) as last_visit
                FROM patient_details p
                JOIN users u ON p.patient_id = u.user_id
                WHERE p.patient_id = ?
            """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, patient.getId());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    this.currentPatient = patient;
                    
                    // Set basic info
                    nameLabel.setText(rs.getString("name"));
                    emailLabel.setText(rs.getString("email"));
                    
                    // Set medical info
                    LocalDate dob = rs.getDate("dob").toLocalDate();
                    int age = Period.between(dob, LocalDate.now()).getYears();
                    ageLabel.setText(String.format("%d years (%s)", age, dob.format(dateFormatter)));
                    genderLabel.setText(rs.getString("gender"));
                    contactLabel.setText(rs.getString("contact"));
                    medicalHistoryLabel.setText(rs.getString("medical_history"));
                   
                    
                    // Set doctor and visit info
                    assignedDoctorLabel.setText(rs.getString("doctor_name"));
                    Timestamp lastVisit = rs.getTimestamp("last_visit");
                    lastVisitLabel.setText(lastVisit != null ? 
                        lastVisit.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : 
                        "No visits yet");
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load profile: " + e.getMessage());
            e.printStackTrace();
        }
        setEditMode(false);
    }

    @FXML
    public void handleEditProfile(ActionEvent actionEvent) {
        if (!isEditMode) {
            // Enter Edit Mode: fill fields with current data
            dobPicker.setValue(currentPatient.getDob());
            genderField.setValue(currentPatient.getGender());
            contactField.setText(currentPatient.getContact());
            addressField.setText(currentPatient.getAddress());
            medicalHistoryArea.setText(currentPatient.getMedicalHistory());
          
        } else {
            if (validateInputs()) {
                // Save Mode: update patient data
                try {
                    currentPatient.setDob(dobPicker.getValue());
                    currentPatient.setGender(genderField.getValue());
                   
                    currentPatient.setContact(contactField.getText().trim());
                    
                    currentPatient.setAddress(addressField.getText().trim());
                    currentPatient.setMedicalHistory(medicalHistoryArea.getText().trim());
                 
                    
                    if (currentPatient.saveToDatabase()) {
                        updateLabels(currentPatient);
                        setEditMode(false);
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile in database.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile: " + e.getMessage());
                }
            }
        }
    }

    private boolean validateInputs() {
        if (dobPicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a date of birth.");
            return false;
        }

        if (dobPicker.getValue().isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Date of birth cannot be in the future.");
            return false;
        }

        if (genderField.getValue() == null || genderField.getValue().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a gender.");
            return false;
        }

        if (contactField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Contact number is required.");
            return false;
        }

        if (addressField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Address is required.");
            return false;
        }

        return true;
    }

    private void updateLabels(Patient patient) {
        int age = patient.getAge();  // Assuming you have this method in Patient class
        ageLabel.setText(String.format("%d years (%s)", age, patient.getDob().format(dateFormatter)));
        genderLabel.setText(patient.getGender());
        contactLabel.setText(patient.getContact());

        addressLabel.setText(patient.getAddress());
        medicalHistoryLabel.setText(patient.getMedicalHistory());
    
    }

    private void setEditMode(boolean enable) {
        isEditMode = enable;

        // Toggle visibility for all labels and edit fields
        nameLabel.setVisible(!enable);
        emailLabel.setVisible(!enable);
        ageLabel.setVisible(!enable);
        genderLabel.setVisible(!enable);
        
        contactLabel.setVisible(!enable);
        
        addressLabel.setVisible(!enable);
        medicalHistoryLabel.setVisible(!enable);
       
        // Toggle edit fields
        dobPicker.setVisible(enable);
        genderField.setVisible(enable);
        contactField.setVisible(enable);
        addressField.setVisible(enable);
        medicalHistoryArea.setVisible(enable);

        editSaveButton.setText(enable ? "Save" : "Edit");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
