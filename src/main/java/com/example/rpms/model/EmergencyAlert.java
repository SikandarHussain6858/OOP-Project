package com.example.rpms.model;

public class EmergencyAlert {
    private double threshold;
    private NotificationService notificationService; // Ensure NotificationService is a valid class or interface

    public EmergencyAlert(double threshold, NotificationService notificationService) {
        this.threshold = threshold;
        this.notificationService = notificationService;
    }
    public void processVitals(String patientname, double currentvalue){
        System.out.println("Processing vitals for " + patientname + ": " + currentvalue);
        if (currentvalue > threshold) {
            System.out.println("Alert! " + patientname + "'s vitals exceeded the threshold of " + threshold);
            System.out.println("Sending notification...");
            notificationService.notify();
        } else {
            System.out.println("No alert. " + patientname + "'s vitals are within the normal range.");
        }
    }
}
