package com.example.rpms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import com.example.rpms.model.DatabaseConnector;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;

public class ViewPatientsController {

    @FXML private TableView<Object[]> patientTable;
    @FXML private TableColumn<Object[], Integer> idColumn;
    @FXML private TableColumn<Object[], String> nameColumn;
    @FXML private TableColumn<Object[], String> emailColumn;
    @FXML private TableColumn<Object[], String> phoneColumn;
    @FXML private TableColumn<Object[], String> dobColumn;
    @FXML private TableColumn<Object[], String> genderColumn;
    @FXML private TableColumn<Object[], Void> actionColumn;
    @FXML private Label statusLabel;

    private ObservableList<Object[]> patientList = FXCollections.observableArrayList();
    private String doctorId;

    @FXML
    public void initialize() {
        setupColumns();
        loadPatients();
    }

    private void setupColumns() {
        // Fix for ID column type casting
        idColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty((Integer) data.getValue()[0]).asObject());
        
        // Rest of the columns remain the same
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

        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<Object[], Void>() {
            private final Button removeButton = new Button("Remove");
            {
                removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                removeButton.setOnAction(evt -> {
                    Object[] patient = getTableRow().getItem();
                    if (patient != null) {
                        handleRemovePatient((Integer) patient[0]);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadPatients();
    }

    private void loadPatients() {
        patientList.clear();
        String sql = "SELECT u.user_id, u.username, u.email, pd.contact, pd.dob, pd.gender " +
                    "FROM users u JOIN patient_details pd ON u.user_id = pd.patient_id " +
                    "WHERE u.role = 'PATIENT'";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("contact"),
                    rs.getDate("dob").toString(),
                    rs.getString("gender")
                };
                patientList.add(row);
            }
            patientTable.setItems(patientList);
            showSuccess("Patients loaded successfully");
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }

    private void handleRemovePatient(int patientId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Patient");
        alert.setContentText("Are you sure you want to remove this patient?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // First delete from patient_details (cascade will handle the user deletion)
                    String sql = "DELETE FROM users WHERE user_id = ? AND role = 'PATIENT'";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, patientId);
                        int affected = stmt.executeUpdate();
                        if (affected > 0) {
                            conn.commit();
                            loadPatients(); // Refresh the table
                            showSuccess("Patient removed successfully");
                        } else {
                            conn.rollback();
                            showError("Failed to remove patient");
                        }
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                showError("Database error: " + e.getMessage());
            }
        }
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        loadPatientsForDoctor();
    }

    private void loadPatientsForDoctor() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = """
                SELECT DISTINCT p.patient_id, u.username, u.email, pd.contact, pd.dob, pd.gender
                FROM patient_details pd
                JOIN users u ON pd.patient_id = u.user_id
                JOIN appointments a ON a.patient_id = pd.patient_id
                WHERE a.doctor_id = ?
            """;
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            patientList.clear();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("patient_id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("contact"),
                    rs.getDate("dob") != null ? rs.getDate("dob").toString() : "",
                    rs.getString("gender")
                };
                patientList.add(row);
            }
            patientTable.setItems(patientList);
            
        } catch (SQLException e) {
            showError("Error loading patients: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
    }
}
