package com.example.rpms.model;

public class ChatService {
    public void startChat(Patient patient, Doctor doctor) {
        System.out.println("Chat started between " + patient.getName() + " and " + doctor.getName());
        // Later: Implement real-time chat
    }
}
