package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDateTime;

public class ViewPatientFeedbackController {
    @FXML private TableView<FeedbackEntry> feedbackTable;
    @FXML private TableColumn<FeedbackEntry, LocalDateTime> dateColumn;
    @FXML private TableColumn<FeedbackEntry, String> patientColumn;
    @FXML private TableColumn<FeedbackEntry, String> feedbackColumn;

    private ObservableList<FeedbackEntry> feedback = FXCollections.observableArrayList();
    private String doctorId;

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));
        
        feedbackTable.setItems(feedback);
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        loadFeedback();
    }

    @FXML
    private void handleRefresh() {
        loadFeedback();
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) feedbackTable.getScene().getWindow();
        stage.close();
    }

    private void loadFeedback() {
        feedback.clear();
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT f.date, u.username as patient_name, f.feedback
                FROM feedbacks f
                JOIN users u ON f.patient_id = u.user_id
                WHERE f.doctor_id = ?
                ORDER BY f.date DESC
                """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                feedback.add(new FeedbackEntry(
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("patient_name"),
                    rs.getString("feedback")
                ));
            }
        } catch (SQLException e) {
            showError("Error loading feedback: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Internal class to represent feedback entries
    public static class FeedbackEntry {
        private final LocalDateTime date;
        private final String patientName;
        private final String feedback;

        public FeedbackEntry(LocalDateTime date, String patientName, String feedback) {
            this.date = date;
            this.patientName = patientName;
            this.feedback = feedback;
        }

        public LocalDateTime getDate() { return date; }
        public String getPatientName() { return patientName; }
        public String getFeedback() { return feedback; }
    }
}
