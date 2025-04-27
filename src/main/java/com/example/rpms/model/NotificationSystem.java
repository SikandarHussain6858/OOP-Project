package com.example.rpms.model;

public class NotificationSystem implements Notifiable {

    @Override
    public void sendNotification(String message, String recipient) {
        System.out.println("Sending notification to " + recipient + ": " + message);
        // Later: Add email or SMS sending logic here
    }
}
