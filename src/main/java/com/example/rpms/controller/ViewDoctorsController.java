package com.example.rpms.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import com.example.rpms.model.DatabaseConnector;

public class ViewDoctorsController {

    @FXML private TableView<Object[]> doctorTable;
    @FXML private TableColumn<Object[], Integer> idColumn;
    @FXML private TableColumn<Object[], String> nameColumn;
    @FXML private TableColumn<Object[], String> specialtyColumn;
    @FXML private TableColumn<Object[], String> emailColumn;
    @FXML private TableColumn<Object[], String> phoneColumn;
    @FXML private TableColumn<Object[], Void> actionColumn;
    @FXML private Label statusLabel;

    private ObservableList<Object[]> doctorList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
                System.out.println("Initializing ViewDoctorsController");
        if (doctorTable == null) {
            System.err.println("doctorTable is null");
        }
        if (idColumn == null) {
            System.err.println("idColumn is null");
        }
        // ... check other columns ...
        
        setupColumns();
        loadDoctors();
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleObjectProperty<Integer>((Integer) data.getValue()[0]));
        nameColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[1]));
        specialtyColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[2]));
        emailColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[3]));
        phoneColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[4]));

        setupActionColumn();
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<Object[], Void>() {
            private final Button removeButton = new Button("Remove");
            {
                removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                removeButton.setOnAction(evt -> {
                    Object[] doctor = getTableRow().getItem();
                    if (doctor != null) {
                        handleRemoveDoctor((Integer) doctor[0]);
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
        loadDoctors();
    }

    private void loadDoctors() {
        doctorList.clear();
        String sql = "SELECT u.user_id, u.username, u.email, dd.specialization, dd.phone_number " +
                    "FROM users u JOIN doctor_details dd ON u.user_id = dd.doctor_id " +
                    "WHERE u.role = 'DOCTOR'";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("specialization"),
                    rs.getString("email"),
                    rs.getString("phone_number")
                };
                doctorList.add(row);
            }
            doctorTable.setItems(doctorList);
            showSuccess("Doctors loaded successfully");
        } catch (SQLException e) {
            showError("Error loading doctors: " + e.getMessage());
        }
    }

    private void handleRemoveDoctor(int doctorId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Removal");
        alert.setHeaderText("Remove Doctor");
        alert.setContentText("Are you sure you want to remove this doctor?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    String sql = "DELETE FROM users WHERE user_id = ? AND role = 'DOCTOR'";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, doctorId);
                        int affected = stmt.executeUpdate();
                        if (affected > 0) {
                            conn.commit();
                            loadDoctors();
                            showSuccess("Doctor removed successfully");
                        } else {
                            conn.rollback();
                            showError("Failed to remove doctor");
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

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
    }
}
