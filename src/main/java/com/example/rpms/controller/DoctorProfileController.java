package com.example.rpms.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class DoctorProfileController implements Initializable {

    @FXML
    private Label nameLabel;

    @FXML
    private Label specializationLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label contactLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label experienceLabel;

    @FXML
    private Label assignedPatientsLabel;

    @FXML
    private Label remarksLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // For now, we’ll use mock data. Replace this with real data fetching logic later.
        nameLabel.setText("Dr. Ayesha Siddiqui");
        specializationLabel.setText("Cardiology");
        emailLabel.setText("ayesha.siddiqui@hospital.com");
        contactLabel.setText("+92-300-1234567");
        departmentLabel.setText("Heart & Vascular");
        experienceLabel.setText("12");
        assignedPatientsLabel.setText("18");
        remarksLabel.setText("Known for accurate diagnoses and patient care.");
    }

    @FXML
    private void handleEditProfile(ActionEvent event) {
        // Placeholder action
        System.out.println("Edit Profile button clicked. Open Edit Form here.");
        // TODO: Implement Edit Profile window or switch scene
    }
}
