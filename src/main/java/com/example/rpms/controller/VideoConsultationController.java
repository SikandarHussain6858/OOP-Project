package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VideoConsultationController {
    @FXML private ComboBox<PatientEntry> patientComboBox;
    @FXML private DatePicker consultationDate;
    @FXML private ComboBox<String> timeSlotComboBox;
    @FXML private ComboBox<String> durationComboBox;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;
    @FXML private Button scheduleButton;
    @FXML private Button cancelButton;
    
    private String doctorId;

    @FXML
    public void initialize() {
        consultationDate.setValue(LocalDate.now());
        
        // Setup time slots
        List<String> timeSlots = new ArrayList<>();
        LocalTime time = LocalTime.of(9, 0); // Start at 9 AM
        while (time.isBefore(LocalTime.of(17, 0))) { // End at 5 PM
            timeSlots.add(time.toString());
            time = time.plusMinutes(30);
        }
        timeSlotComboBox.setItems(FXCollections.observableArrayList(timeSlots));
        
        // Setup durations
        durationComboBox.setItems(FXCollections.observableArrayList(
            "15 minutes", "30 minutes", "45 minutes", "1 hour"
        ));
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        loadPatients();
    }
    
    private void loadPatients() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT DISTINCT p.patient_id, u.username as name
                FROM patient_details p
                JOIN users u ON p.patient_id = u.user_id
                JOIN appointments a ON p.patient_id = a.patient_id
                WHERE a.doctor_id = ?
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            List<PatientEntry> patients = new ArrayList<>();
            while (rs.next()) {
                patients.add(new PatientEntry(
                    rs.getString("patient_id"),
                    rs.getString("name")
                ));
            }
            patientComboBox.setItems(FXCollections.observableArrayList(patients));
            
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSchedule() {
        if (!validateInputs()) {
            return;
        }
        
        PatientEntry selectedPatient = patientComboBox.getValue();
        LocalDate date = consultationDate.getValue();
        String time = timeSlotComboBox.getValue();
        String duration = durationComboBox.getValue();
        String notes = notesArea.getText().trim();
        
        // Generate Google Meet link
        String meetLink = generateMeetLink();
        
        // Save consultation to database
        if (saveConsultation(selectedPatient.getId(), date, time, duration, notes, meetLink)) {
            // Send email to patient
            sendConsultationEmail(selectedPatient.getId(), selectedPatient.getName(), date, time, duration, notes, meetLink);
            showSuccess("Video consultation scheduled and link sent to patient");
            clearFields();
        }
    }
    
    private boolean saveConsultation(String patientId, LocalDate date, String time, String duration, String notes, String meetLink) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                INSERT INTO video_consultations 
                (patient_id, doctor_id, consultation_date, consultation_time, 
                duration, notes, meet_link, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW())
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            stmt.setString(2, doctorId);
            stmt.setDate(3, Date.valueOf(date));
            stmt.setString(4, time);
            stmt.setString(5, duration);
            stmt.setString(6, notes);
            stmt.setString(7, meetLink);
            
            int result = stmt.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            showError("Error saving consultation: " + e.getMessage());
            return false;
        }
    }
    
    private void sendConsultationEmail(String patientId, String patientName, LocalDate date, 
                                     String time, String duration, String notes, String meetLink) {
        String subject = "Video Consultation Scheduled";
        String body = String.format("""
            Dear %s,
            
            Your video consultation has been scheduled.
            
            Date: %s
            Time: %s
            Duration: %s
            
            Join using this link: %s
            
            Additional Notes:
            %s
            
            Best regards,
            Your Doctor
            """, patientName, date, time, duration, meetLink, notes);
            
        // Get patient's email from database
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT email FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String patientEmail = rs.getString("email");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/email.fxml"));
                try {
                    Parent root = loader.load();
                    EmailController emailController = loader.getController();
                    emailController.prepareVideoCallEmail(patientEmail, subject, body);
                    
                    Stage stage = new Stage();
                    stage.setTitle("Send Video Consultation Email");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    showError("Error opening email form: " + e.getMessage());
                }
            } else {
                showError("Could not find patient's email address");
            }
        } catch (SQLException e) {
            showError("Database error while sending email: " + e.getMessage());
        }
    }
    
    private String generateMeetLink() {
        // This would integrate with Google Meet API in a real implementation
        // For now, return a dummy link
        return "https://meet.google.com/abc-defg-hij";
    }
    
    @FXML
    private void handleCancel() {
        clearFields();
    }
    
    private boolean validateInputs() {
        if (patientComboBox.getValue() == null) {
            showError("Please select a patient");
            return false;
        }
        
        if (consultationDate.getValue() == null) {
            showError("Please select a date");
            return false;
        }
        
        if (timeSlotComboBox.getValue() == null) {
            showError("Please select a time slot");
            return false;
        }
        
        if (durationComboBox.getValue() == null) {
            showError("Please select duration");
            return false;
        }
        
        if (consultationDate.getValue().isBefore(LocalDate.now())) {
            showError("Please select a future date");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        patientComboBox.setValue(null);
        consultationDate.setValue(LocalDate.now());
        timeSlotComboBox.setValue(null);
        durationComboBox.setValue(null);
        notesArea.clear();
        statusLabel.setText("");
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
    
    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71;");
    }
    
    private static class PatientEntry {
        private final String id;
        private final String name;
        
        public PatientEntry(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        
        @Override
        public String toString() {
            return name;
        }
    }
}
