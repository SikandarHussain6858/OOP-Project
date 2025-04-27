package com.example.rpms.model;

public class ChatServer {
    public void startChat(Patient patient, Doctor doctor) {
        System.out.println("Chat started between " + patient.getName() + " and " + doctor.getName());
        // Later: Implement real-time chat
    }
    public void receiveMessage(String from, String to, String message) {
        System.out.println("Message from " + from + " to " + to + ": " + message);
    }
}
