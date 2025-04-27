package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ReportViewController {

    @FXML
    private TextArea reportArea;

    @FXML
    private Button generateReportButton;

    @FXML
    private void handleGenerateReportAction() {
        reportArea.setText("Sample Report:\n\nVitals History\nDoctor Feedback\nMedication Records\n...");
    }
}
