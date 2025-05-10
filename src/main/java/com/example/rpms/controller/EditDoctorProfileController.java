package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EditDoctorProfileController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField specializationField;
    @FXML private TextField qualificationField;
    @FXML private TextField experienceField;
    @FXML private TextArea addressField;
    @FXML private TextArea remarksField;
    @FXML private Label statusLabel;

    private String doctorId;
    private DoctorProfileController profileController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize UI components
    }

    public void setDoctorId(String id) {
        this.doctorId = id;
        loadDoctorData();
    }

    public void setProfileController(DoctorProfileController controller) {
        this.profileController = controller;
    }

    private void loadDoctorData() {
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
                nameField.setText(rs.getString("username"));
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("phone_number"));
                specializationField.setText(rs.getString("specialization"));
                qualificationField.setText(rs.getString("qualification"));
                experienceField.setText(String.valueOf(rs.getInt("years_of_experience")));
                addressField.setText(rs.getString("address"));
                // Remarks might be null
                String remarks = rs.getString("remarks");
                remarksField.setText(remarks != null ? remarks : "");
            }
        } catch (SQLException e) {
            showError("Error loading doctor data: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update users table
                String userSql = "UPDATE users SET username = ?, email = ? WHERE user_id = ?";
                PreparedStatement userStmt = conn.prepareStatement(userSql);
                userStmt.setString(1, nameField.getText().trim());
                userStmt.setString(2, emailField.getText().trim());
                userStmt.setString(3, doctorId);
                userStmt.executeUpdate();

                // Update doctor_details table
                String detailsSql = """
                    UPDATE doctor_details 
                    SET specialization = ?, phone_number = ?, qualification = ?,
                        years_of_experience = ?, address = ?, remarks = ?
                    WHERE doctor_id = ?
                """;
                
                PreparedStatement detailsStmt = conn.prepareStatement(detailsSql);
                detailsStmt.setString(1, specializationField.getText().trim());
                detailsStmt.setString(2, phoneField.getText().trim());
                detailsStmt.setString(3, qualificationField.getText().trim());
                detailsStmt.setInt(4, Integer.parseInt(experienceField.getText().trim()));
                detailsStmt.setString(5, addressField.getText().trim());
                detailsStmt.setString(6, remarksField.getText().trim());
                detailsStmt.setString(7, doctorId);
                detailsStmt.executeUpdate();

                conn.commit();
                showSuccess("Profile updated successfully!");
                
                // Refresh the profile view
                if (profileController != null) {
                    profileController.refreshProfile();
                }
                
                // Close the edit window
                ((Stage) nameField.getScene().getWindow()).close();
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            showError("Error updating profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (nameField.getText().trim().isEmpty()) {
            errors.append("Name is required\n");
        }
        if (emailField.getText().trim().isEmpty()) {
            errors.append("Email is required\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("Invalid email format\n");
        }
        if (phoneField.getText().trim().isEmpty()) {
            errors.append("Phone number is required\n");
        } else if (!phoneField.getText().trim().matches("\\d{10,11}")) {
            errors.append("Invalid phone number format\n");
        }
        if (specializationField.getText().trim().isEmpty()) {
            errors.append("Specialization is required\n");
        }
        if (qualificationField.getText().trim().isEmpty()) {
            errors.append("Qualification is required\n");
        }
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
        if (addressField.getText().trim().isEmpty()) {
            errors.append("Address is required\n");
        }

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
}
