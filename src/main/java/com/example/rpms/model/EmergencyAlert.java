package com.example.rpms.model;

import java.time.LocalDateTime;

public class EmergencyAlert {
    private double threshold;
    private NotificationService notificationService; // Ensure NotificationService is a valid class or interface
    private final LocalDateTime createdAt;
    private final String alertType;
    private final String message;
    private final String status;
    private String patientName;  // Optional, used for doctor view

    public EmergencyAlert(double threshold, NotificationService notificationService, LocalDateTime createdAt, String alertType, String message, String status) {
        this.threshold = threshold;
        this.notificationService = notificationService;
        this.createdAt = createdAt;
        this.alertType = alertType;
        this.message = message;
        this.status = status;
    }

    public double getThreshold() {
        return threshold;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAlertType() {
        return alertType;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}