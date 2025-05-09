package com.example.rpms.controller;

import com.example.rpms.model.Patient;
import com.example.rpms.model.Vitals;
import com.example.rpms.model.Feedback;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import java.util.List;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ViewReportController{

    @FXML private Text patientInfoText;
    @FXML private Text vitalsText;
    @FXML private Text feedbackText;

    private Patient currentPatient;
    private String patientId;

    public void setPatient(Patient patient) {
        this.currentPatient = patient;
        displayReport();
    }
    
    public void setPatient(String patientId) {
        this.patientId = patientId;
    }

    private void displayReport() {
        if (currentPatient == null) return;

        // Calculate age from DOB
        int age = Period.between(currentPatient.getDob(), LocalDate.now()).getYears();

        // Patient Info
        String info = String.format("""
                Name: %s
                Email: %s
                Age: %d
                Gender: %s
                Contact: %s
                Address: %s
                """,
                currentPatient.getName(),
                currentPatient.getEmail(),
                age,
                currentPatient.getGender(),
                currentPatient.getContact(),
                currentPatient.getAddress()
        );
        patientInfoText.setText(info);

        // Vitals - show last 3
        List<Vitals> vitals = currentPatient.getVitals();
        StringBuilder vitalsReport = new StringBuilder();
        if (vitals != null && !vitals.isEmpty()) {
            int start = Math.max(vitals.size() - 3, 0);
            for (int i = vitals.size() - 1; i >= start; i--) {
                Vitals v = vitals.get(i);
                vitalsReport.append(String.format("""
                        • Date: %s
                          Heart Rate: %.1f bpm
                          Oxygen: %.1f %%
                          BP: %.1f/%.1f mmHg
                          Temp: %.1f °C

                        """,
                        v.getRecordedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        v.getHeartRate(),
                        v.getOxygenSaturation(),
                        v.getBloodPressureSystolic(),
                        v.getBloodPressureDiastolic(),
                        v.getTemperature()));
            }
        }
        vitalsText.setText(vitalsReport.isEmpty() ? "No vitals recorded." : vitalsReport.toString());

        // Feedbacks - latest 2
        List<Feedback> feedbacks = currentPatient.getFeedbacks();
        StringBuilder feedbackReport = new StringBuilder();
        if (feedbacks != null && !feedbacks.isEmpty()) {
            int start = Math.max(feedbacks.size() - 2, 0);
            for (int i = feedbacks.size() - 1; i >= start; i--) {
                Feedback f = feedbacks.get(i);
                feedbackReport.append(String.format("• %s\n  Date: %s\n\n",
                    f.getComments(),
                    f.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            }
        }
        feedbackText.setText(feedbackReport.isEmpty() ? "No feedback available." : feedbackReport.toString());
    }
}
