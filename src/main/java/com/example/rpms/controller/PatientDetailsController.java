package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDateTime;
import com.example.rpms.model.DatabaseConnector;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class PatientDetailsController {
    @FXML private Text patientInfoText;
    @FXML private Text vitalsText;
    @FXML private Label lastUpdatedLabel;
    @FXML private TableView<EmergencyAlert> alertsTable;
    @FXML private TableColumn<EmergencyAlert, LocalDateTime> alertDateColumn;
    @FXML private TableColumn<EmergencyAlert, String> alertTypeColumn;
    @FXML private TableColumn<EmergencyAlert, String> alertMessageColumn;
    @FXML private TableColumn<EmergencyAlert, String> alertStatusColumn;
    @FXML private TableView<FeedbackEntry> feedbackTable;
    @FXML private TableColumn<FeedbackEntry, LocalDateTime> feedbackDateColumn;
    @FXML private TableColumn<FeedbackEntry, String> doctorNameColumn;
    @FXML private TableColumn<FeedbackEntry, String> feedbackTextColumn;

    private String patientId;
    private String doctorId;
    private final ObservableList<EmergencyAlert> alerts = FXCollections.observableArrayList();
    private final ObservableList<FeedbackEntry> feedbacks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupEmergencyAlertsTable();
        setupFeedbackTable();
    }

    private void setupEmergencyAlertsTable() {
        alertDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        alertTypeColumn.setCellValueFactory(new PropertyValueFactory<>("alertType"));
        alertMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        alertStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        alertsTable.setItems(alerts);
    }

    private void setupFeedbackTable() {
        feedbackDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        doctorNameColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        feedbackTextColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        feedbackTable.setItems(feedbacks);
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
        loadAllData();
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    private void loadAllData() {
        loadPatientInfo();
        loadLatestVitals();
        loadEmergencyAlerts();
        loadFeedbackHistory();
    }

    @FXML
    private void handleRefreshVitals() {
        loadLatestVitals();
    }

    @FXML
    private void handleRefreshAlerts() {
        loadEmergencyAlerts();
    }

    @FXML
    private void handleRefreshFeedback() {
        loadFeedbackHistory();
    }

    @FXML
    private void handleAddFeedback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/add_feedback.fxml"));
            Parent root = loader.load();
            
            AddFeedbackController controller = loader.getController();
            controller.setPatientId(patientId);
            controller.setDoctorId(doctorId);
            
            Stage stage = new Stage();
            stage.setTitle("Add Feedback");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh feedback list when the window is closed
            stage.setOnHidden(e -> loadFeedbackHistory());
        } catch (IOException e) {
            showError("Error opening feedback form: " + e.getMessage());
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
                String info = String.format("""
                    Name: %s
                    Email: %s
                    Date of Birth: %s
                    Gender: %s
                    Contact: %s
                    Address: %s""",
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getDate("dob"),
                    rs.getString("gender"),
                    rs.getString("contact"),
                    rs.getString("address")
                );
                patientInfoText.setText(info);
            } else {
                patientInfoText.setText("No patient information found");
            }
        } catch (SQLException e) {
            showError("Error loading patient info: " + e.getMessage());
        }
    }

    private void loadLatestVitals() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT heart_rate, oxygen_saturation, bp_systolic, bp_diastolic, temperature, recorded_at
                FROM patient_vitals 
                WHERE patient_id = ? 
                ORDER BY recorded_at DESC LIMIT 1
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String vitals = String.format("""
                    Heart Rate: %d bpm
                    Oxygen Saturation: %d%%
                    Blood Pressure: %d/%d mmHg
                    Temperature: %.1f°C""",
                    rs.getInt("heart_rate"),
                    rs.getInt("oxygen_saturation"),
                    rs.getInt("bp_systolic"),
                    rs.getInt("bp_diastolic"),
                    rs.getDouble("temperature")
                );
                vitalsText.setText(vitals);
                lastUpdatedLabel.setText("Last updated: " + rs.getTimestamp("recorded_at"));
            } else {
                vitalsText.setText("No vitals data available");
                lastUpdatedLabel.setText("");
            }
        } catch (SQLException e) {
            showError("Error loading vitals: " + e.getMessage());
        }
    }

    private void loadEmergencyAlerts() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT alert_id, alert_type, alert_message, status, created_at 
                FROM emergency_alerts 
                WHERE patient_id = ? 
                ORDER BY created_at DESC
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            alerts.clear();
            while (rs.next()) {
                alerts.add(new EmergencyAlert(
                    rs.getInt("alert_id"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("alert_type"),
                    rs.getString("alert_message"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading emergency alerts: " + e.getMessage());
        }
    }

    private void loadFeedbackHistory() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT f.feedback_id, f.feedback, f.date, u.username as doctor_name 
                FROM feedbacks f 
                JOIN users u ON f.doctor_id = u.user_id 
                WHERE f.patient_id = ? 
                ORDER BY f.date DESC
                """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            feedbacks.clear();
            while (rs.next()) {
                feedbacks.add(new FeedbackEntry(
                    rs.getInt("feedback_id"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("doctor_name"),
                    rs.getString("feedback")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading feedback history: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class EmergencyAlert {
        private final int alertId;
        private final LocalDateTime createdAt;
        private final String alertType;
        private final String message;
        private final String status;

        public EmergencyAlert(int alertId, LocalDateTime createdAt, String alertType, String message, String status) {
            this.alertId = alertId;
            this.createdAt = createdAt;
            this.alertType = alertType;
            this.message = message;
            this.status = status;
        }

        public int getAlertId() { return alertId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public String getAlertType() { return alertType; }
        public String getMessage() { return message; }
        public String getStatus() { return status; }
    }

    public static class FeedbackEntry {
        private final int feedbackId;
        private final LocalDateTime date;
        private final String doctorName;
        private final String feedback;

        public FeedbackEntry(int feedbackId, LocalDateTime date, String doctorName, String feedback) {
            this.feedbackId = feedbackId;
            this.date = date;
            this.doctorName = doctorName;
            this.feedback = feedback;
        }

        public int getFeedbackId() { return feedbackId; }
        public LocalDateTime getDate() { return date; }
        public String getDoctorName() { return doctorName; }
        public String getFeedback() { return feedback; }
    }
}
