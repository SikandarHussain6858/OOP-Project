package com.example.rpms.model;

public interface Notifiable {

    void sendNotification(String message, String recipient);
    void notify(String message, String recipient);

}
