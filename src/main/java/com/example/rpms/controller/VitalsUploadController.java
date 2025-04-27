package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;

public class VitalsUploadController {

    @FXML
    private Button uploadButton;

    @FXML
    private void handleUploadButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Vitals CSV File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            System.out.println("File selected: " + file.getAbsolutePath());
            // Later: Parse CSV and load vitals
        }
    }
}