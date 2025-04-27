package com.example.rpms.model;

public class PanicButton {
    private NotificationService notificationService;
    public PanicButton(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    public void triggerAlert(String patientname){
        System.out.println("Panic button triggered for " + patientname);
        System.out.println("Sending emergency alert...");
        notificationService.alert("Emergency alert for " + patientname, "hospital");
        System.out.println("Alert sent to hospital.");
    }
}

