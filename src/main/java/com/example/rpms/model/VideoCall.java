package com.example.rpms.model;

public class VideoCall {
    public void startCall(String platform) {
        if (platform.equalsIgnoreCase("Zoom")) {
            System.out.println("Join the Zoom call: https://zoom.us/example");
        } else if (platform.equalsIgnoreCase("Google Meet")) {
            System.out.println("Join the Meet call: https://meet.google.com/example");
        } else {
            System.out.println("Invalid platform.");
        }
    }

}
