package com.example.rpms.model;

import java.time.LocalDate;
import javafx.beans.property.*;

public class Document {
    private final IntegerProperty documentId = new SimpleIntegerProperty();
    private final IntegerProperty patientId = new SimpleIntegerProperty();
    private final StringProperty documentType = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> documentDate = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty filePath = new SimpleStringProperty();

    public Document(int documentId, int patientId, String documentType, 
                   LocalDate documentDate, String description, String filePath) {
        setDocumentId(documentId);
        setPatientId(patientId);
        setDocumentType(documentType);
        setDocumentDate(documentDate);
        setDescription(description);
        setFilePath(filePath);
    }

    // Property getters
    public IntegerProperty documentIdProperty() { return documentId; }
    public IntegerProperty patientIdProperty() { return patientId; }
    public StringProperty documentTypeProperty() { return documentType; }
    public ObjectProperty<LocalDate> documentDateProperty() { return documentDate; }
    public StringProperty descriptionProperty() { return description; }
    public StringProperty filePathProperty() { return filePath; }

    // Value getters
    public int getDocumentId() { return documentId.get(); }
    public int getPatientId() { return patientId.get(); }
    public String getDocumentType() { return documentType.get(); }
    public LocalDate getDocumentDate() { return documentDate.get(); }
    public String getDescription() { return description.get(); }
    public String getFilePath() { return filePath.get(); }

    // Setters
    public void setDocumentId(int value) { documentId.set(value); }
    public void setPatientId(int value) { patientId.set(value); }
    public void setDocumentType(String value) { documentType.set(value); }
    public void setDocumentDate(LocalDate value) { documentDate.set(value); }
    public void setDescription(String value) { description.set(value); }
    public void setFilePath(String value) { filePath.set(value); }
}