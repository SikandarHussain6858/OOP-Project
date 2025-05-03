package com.example.rpms.controller;

import com.example.rpms.model.Administrator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import com.example.rpms.model.Appointment;
import com.example.rpms.model.Doctor;
import com.example.rpms.model.Patient;

public class AdminDashboardController {

    @FXML
    private VBox mainContent;

    @FXML
    private void handleAddPatient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/addPatientform.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Patient");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemovePatient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/removePatient.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Remove Patient");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDoctor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/addDoctorform.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add Doctor");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveDoctor(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/removeDoctor.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Remove Doctor");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBookAppointment(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/bookAppointment.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Book Appointment");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPatients(ActionEvent event) {
        mainContent.getChildren().clear();

        Label title = new Label("Patients List");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<Patient> table = new TableView<>();

        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        table.getColumns().addAll(idCol, nameCol, ageCol);

        List<Patient> patientList = Administrator.viewPatients();
        if (patientList != null) {
            ObservableList<Patient> data = FXCollections.observableArrayList(patientList);
            table.setItems(data);
        }

        mainContent.getChildren().addAll(title, table);
    }

    @FXML
    private void handleViewDoctors(ActionEvent event) {
        mainContent.getChildren().clear();

        Label title = new Label("Doctors List");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<Doctor> table = new TableView<>();

        TableColumn<Doctor, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Doctor, String> specialtyCol = new TableColumn<>("Specialty");
        specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));

        table.getColumns().addAll(idCol, nameCol, specialtyCol);

        ObservableList<Doctor> data = FXCollections.observableArrayList(
                new Doctor("D001", "Dr. Alice", "Cardiology"),
                new Doctor("D002", "Dr. Bob", "Neurology")
        );

        table.setItems(data);

        mainContent.getChildren().addAll(title, table);
    }

    @FXML
    private void handleViewAppointments(ActionEvent event) {
        mainContent.getChildren().clear();

        Label title = new Label("Appointments List");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<Appointment> table = new TableView<>();

        TableColumn<Appointment, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.getColumns().addAll(idCol, patientCol, doctorCol, dateCol);

        // You should fetch actual appointments from your model here
        // ObservableList<Appointment> data = FXCollections.observableArrayList(...);
        // table.setItems(data);

        mainContent.getChildren().addAll(title, table);
    }

    @FXML
    private void handleSendEmail(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/email.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Send Email");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
