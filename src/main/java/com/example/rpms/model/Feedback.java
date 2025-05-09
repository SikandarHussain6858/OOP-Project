package com.example.rpms.model;

import java.time.LocalDateTime;

public class Feedback {
    private final int feedbackId;
    private final int patientId;
    private final String comments;
    private final LocalDateTime createdAt;

    public Feedback(int feedbackId, int patientId, String comments, LocalDateTime createdAt) {
        this.feedbackId = feedbackId;
        this.patientId = patientId;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public int getFeedbackId() { return feedbackId; }
    public int getPatientId() { return patientId; }
    public String getComments() { return comments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

