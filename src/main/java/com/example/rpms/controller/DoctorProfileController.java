package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorProfileController implements Initializable {
    @FXML private Label nameLabel;
    @FXML private Label specializationLabel;
    @FXML private Label emailLabel;
    @FXML private Label contactLabel;
    @FXML private Label qualificationLabel;
    @FXML private Label experienceLabel;
    @FXML private Label assignedPatientsLabel;
    @FXML private Label remarksLabel;
    @FXML private Button editButton;

    private String doctorId;

    public void setDoctorId(String id) {
        this.doctorId = id;
        loadDoctorProfile();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initial state - labels will be populated when setDoctorId is called
    }

    private void loadDoctorProfile() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT u.username, u.email, d.* 
                FROM users u 
                JOIN doctor_details d ON u.user_id = d.doctor_id 
                WHERE u.user_id = ?
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameLabel.setText(rs.getString("username"));
                specializationLabel.setText(rs.getString("specialization"));
                emailLabel.setText(rs.getString("email"));
                contactLabel.setText(rs.getString("phone_number"));
                qualificationLabel.setText(rs.getString("qualification"));
                experienceLabel.setText(String.valueOf(rs.getInt("years_of_experience")));

                // Load assigned patients count
                String countSql = "SELECT COUNT(DISTINCT patient_id) as patient_count FROM appointments WHERE doctor_id = ?";
                PreparedStatement countStmt = conn.prepareStatement(countSql);
                countStmt.setString(1, doctorId);
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    assignedPatientsLabel.setText(String.valueOf(countRs.getInt("patient_count")));
                }
            }
        } catch (SQLException e) {
            showError("Error loading profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/edit_doctor_profile.fxml"));
            Parent root = loader.load();
            
            EditDoctorProfileController controller = loader.getController();
            controller.setDoctorId(doctorId);
            controller.setProfileController(this); // Allow the edit controller to refresh this view
            
            Stage stage = new Stage();
            stage.setTitle("Edit Profile");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Error opening edit profile: " + e.getMessage());
        }
    }

    public void refreshProfile() {
        loadDoctorProfile();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
