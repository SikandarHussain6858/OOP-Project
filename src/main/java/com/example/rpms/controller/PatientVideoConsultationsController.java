package com.example.rpms.controller;

import com.example.rpms.model.DatabaseConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.awt.Desktop;
import java.net.URI;

public class PatientVideoConsultationsController {
    @FXML private TableView<Object[]> consultationsTable;
    @FXML private TableColumn<Object[], String> dateColumn;
    @FXML private TableColumn<Object[], String> timeColumn;
    @FXML private TableColumn<Object[], String> doctorColumn;
    @FXML private TableColumn<Object[], String> durationColumn;
    @FXML private TableColumn<Object[], String> notesColumn;
    @FXML private TableColumn<Object[], String> meetLinkColumn;
    @FXML private TableColumn<Object[], Void> actionColumn;
    @FXML private Label statusLabel;

    private String patientId;
    private ObservableList<Object[]> consultationsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupColumns();
        setupActionColumn();
    }

    private void setupColumns() {
        dateColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[0]));
        timeColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[1]));
        doctorColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[2]));
        durationColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[3]));
        notesColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[4]));
        meetLinkColumn.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty((String) data.getValue()[5]));
    }

    private void setupActionColumn() {
        actionColumn.setCellFactory(col -> new TableCell<Object[], Void>() {
            private final Button joinButton = new Button("Join");
            {
                joinButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                joinButton.setOnAction(evt -> {
                    Object[] consultation = getTableRow().getItem();
                    if (consultation != null) {
                        handleJoinMeeting((String) consultation[5]);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : joinButton);
            }
        });
    }

    public void setPatientId(String id) {
        this.patientId = id;
        loadConsultations();
    }

    private void loadConsultations() {
        consultationsList.clear();
        String sql = """
            SELECT DATE_FORMAT(vc.consultation_date, '%Y-%m-%d') as date,
                   vc.consultation_time as time,
                   u.username as doctor_name,
                   vc.duration, vc.notes, vc.meet_link
            FROM video_consultations vc
            JOIN users u ON vc.doctor_id = u.user_id
            WHERE vc.patient_id = ?
            ORDER BY vc.consultation_date DESC, vc.consultation_time DESC
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getString("doctor_name"),
                    rs.getString("duration"),
                    rs.getString("notes"),
                    rs.getString("meet_link")
                };
                consultationsList.add(row);
            }
            consultationsTable.setItems(consultationsList);
            
            if (consultationsList.isEmpty()) {
                showInfo("No video consultations scheduled");
            } else {
                showSuccess("Video consultations loaded successfully");
            }
        } catch (SQLException e) {
            showError("Error loading video consultations: " + e.getMessage());
        }
    }

    private void handleJoinMeeting(String meetLink) {
        try {
            Desktop.getDesktop().browse(new URI(meetLink));
        } catch (Exception e) {
            showError("Error opening meeting link: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadConsultations();
    }

    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String message) {
        statusLabel.setText("✅ " + message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71;");
    }

    private void showInfo(String message) {
        statusLabel.setText("ℹ️ " + message);
        statusLabel.setStyle("-fx-text-fill: #3498db;");
    }
}
