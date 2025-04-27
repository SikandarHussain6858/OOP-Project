package com.example.rpms.model;

public class NotificationService {
    private Notifiable notifier;
    public NotificationService(Notifiable notifier) {
        this.notifier = notifier;
    }
    public void alert(String message,String recipient) {
        notifier.notify(message,recipient);

    }

}