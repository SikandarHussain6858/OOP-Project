package com.example.rpms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import com.example.rpms.model.DatabaseConnector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DoctorViewPatientsController {
    @FXML private TextField searchField;
    @FXML private TableView<Object[]> patientTable;
    @FXML private TableColumn<Object[], Integer> idColumn;
    @FXML private TableColumn<Object[], String> nameColumn;
    @FXML private TableColumn<Object[], String> emailColumn;
    @FXML private TableColumn<Object[], String> phoneColumn;
    @FXML private TableColumn<Object[], String> dobColumn;
    @FXML private TableColumn<Object[], String> genderColumn;

    private String doctorId;
    private ObservableList<Object[]> patientList = FXCollections.observableArrayList();
    private FilteredList<Object[]> filteredPatients;

    @FXML
    public void initialize() {
        setupColumns();
        setupSearch();
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty((Integer) data.getValue()[0]).asObject());
        nameColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[1]));
        emailColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[2]));
        phoneColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[3]));
        dobColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[4]));
        genderColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[5]));
    }

    private void setupSearch() {
        filteredPatients = new FilteredList<>(patientList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredPatients.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                String name = ((String) patient[1]).toLowerCase();
                String email = ((String) patient[2]).toLowerCase();
                String phone = ((String) patient[3]).toLowerCase();

                return name.contains(lowerCaseFilter) ||
                       email.contains(lowerCaseFilter) ||
                       phone.contains(lowerCaseFilter);
            });
        });
        patientTable.setItems(filteredPatients);
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        loadPatientsForDoctor();
    }

    private void loadPatientsForDoctor() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT DISTINCT u.user_id, u.username, u.email, pd.contact, pd.dob, pd.gender
                FROM users u
                JOIN patient_details pd ON u.user_id = pd.patient_id
                JOIN appointments a ON a.patient_id = pd.patient_id
                WHERE a.doctor_id = ?
                ORDER BY u.username
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            patientList.clear();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("contact"),
                    rs.getDate("dob") != null ? rs.getDate("dob").toString() : "",
                    rs.getString("gender")
                };
                patientList.add(row);
            }
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        // Search is handled by the FilteredList in setupSearch()
    }

    @FXML
    private void handleViewDetails() {
        Object[] selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showError("Please select a patient first");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/patient_details.fxml"));
            Parent root = loader.load();
            
            PatientDetailsController controller = loader.getController();
            controller.setPatientId(String.valueOf(selectedPatient[0]));
            controller.setDoctorId(doctorId);
            
            Stage stage = new Stage();
            stage.setTitle("Patient Details - " + selectedPatient[1]);
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);  // Set minimum width
            stage.setMinHeight(600); // Set minimum height
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Add this to see detailed error
            showError("Error opening patient details: " + e.getMessage());
        }
    }

    @FXML
    private void handleSendMessage() {
        Object[] selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showError("Please select a patient first");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/email.fxml"));
            Parent root = loader.load();
            
            EmailController controller = loader.getController();
            // Pre-fill the email with patient's email address
            
            Stage stage = new Stage();
            stage.setTitle("Send Message");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error opening message form: " + e.getMessage());
        }
    }

    @FXML
    private void handleWritePrescription() {
        Object[] selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showError("Please select a patient first");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/write_prescription.fxml"));
            Parent root = loader.load();
            
            WritePrescriptionController controller = loader.getController();
            controller.setPatientId(String.valueOf(selectedPatient[0]));
            controller.setDoctorId(doctorId);
            controller.setPatientName((String) selectedPatient[1]);
            
            Stage stage = new Stage();
            stage.setTitle("Write Prescription");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error opening prescription form: " + e.getMessage());
        }
    }

    @FXML
    private void handleStartVideoCall() {
        Object[] selectedPatient = patientTable.getSelectionModel().getSelectedItem();
        if (selectedPatient == null) {
            showError("Please select a patient first");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/email.fxml"));
            Parent root = loader.load();
            
            EmailController controller = loader.getController();
            controller.setUserId(doctorId);
            
            // Pre-fill the email with video call template
            String patientEmail = (String) selectedPatient[2];
            String patientName = (String) selectedPatient[1];
            String subject = "Video Consultation Link";
            String template = """
                Dear %s,

                I have scheduled a video consultation with you. Please join using the Google Meet link below:

                [Paste your Google Meet link here]

                If you have any issues joining the call, please reply to this email or contact the clinic.

                Best regards,
                Dr. [Your name]
                """.formatted(patientName);
            
            controller.prepareVideoCallEmail(patientEmail, subject, template);
            
            Stage stage = new Stage();
            stage.setTitle("Send Video Call Link");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error opening email form: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh() {
        loadPatientsForDoctor();
    }
}
