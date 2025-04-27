package com.example.rpms.model;

public class ChatClient {
    private String username;
    private ChatServer chatServer;

    public ChatClient(String username, ChatServer chatServer) {
        this.username = username;
        this.chatServer = chatServer;
    }
    public void sendMessage(String to, String message) {
        System.out.println("Sending message from " + username + " to " + to + ": " + message);
        chatServer.receiveMessage(username, to, message);
    }
}