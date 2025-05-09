package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.*;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.rpms.model.DatabaseConnector;
import com.example.rpms.model.Document;

public class DocumentManagementController {
    @FXML private ComboBox<String> documentTypeCombo;
    @FXML private DatePicker documentDate;
    @FXML private TextArea documentDescription;
    @FXML private Label selectedFileLabel;
    @FXML private TableView<Document> documentsTable;
    @FXML private TableColumn<Document, LocalDate> dateColumn;
    @FXML private TableColumn<Document, String> typeColumn;
    @FXML private TableColumn<Document, String> descriptionColumn;
    
    private File selectedFile;
    private int patientId;
    private ObservableList<Document> documentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        documentTypeCombo.setItems(FXCollections.observableArrayList(
            "Medical Report", "Test Result", "Prescription", "X-Ray", "MRI Scan", "Lab Report"
        ));
        documentDate.setValue(LocalDate.now());
        setupTable();
        loadDocuments();
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedFileLabel.setText(selectedFile.getName());
        }
    }

    @FXML
    private void handleUpload() {
        if (selectedFile == null || documentTypeCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a file and document type");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO patient_documents (patient_id, document_type, document_date, " +
                        "description, file_path) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, patientId);
                stmt.setString(2, documentTypeCombo.getValue());
                stmt.setDate(3, java.sql.Date.valueOf(documentDate.getValue()));
                stmt.setString(4, documentDescription.getText());
                stmt.setString(5, selectedFile.getAbsolutePath());
                
                stmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Document uploaded successfully");
                clearForm();
                loadDocuments();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to upload document: " + e.getMessage());
        }
    }

    private void setupTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("documentDate"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        documentsTable.setItems(documentList);
    }

    private void loadDocuments() {
        documentList.clear();
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT * FROM patient_documents WHERE patient_id = ? ORDER BY document_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, patientId);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Document doc = new Document(
                        rs.getInt("document_id"),
                        rs.getInt("patient_id"),
                        rs.getString("document_type"),
                        rs.getDate("document_date").toLocalDate(),
                        rs.getString("description"),
                        rs.getString("file_path")
                    );
                    documentList.add(doc);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load documents: " + e.getMessage());
        }
    }

    private void clearForm() {
        documentTypeCombo.setValue(null);
        documentDate.setValue(LocalDate.now());
        documentDescription.clear();
        selectedFile = null;
        selectedFileLabel.setText("");
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
        loadDocuments();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}