package com.example.rpms.model;

import java.time.LocalDateTime;

public class Vitals {
    private int vitalId;
    private int patientId;
    private double bpSystolic;
    private double bpDiastolic;
    private double heartRate;
    private double temperature;
    private double oxygenSaturation;
    private double glucose;
    private LocalDateTime recordedAt;

    // Constructor
    public Vitals(int vitalId, int patientId, double bpSystolic, double bpDiastolic, double heartRate,
                  double temperature, double oxygenSaturation, double glucose, LocalDateTime recordedAt) {
        this.vitalId = vitalId;
        this.patientId = patientId;
        this.bpSystolic = bpSystolic;
        this.bpDiastolic = bpDiastolic;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.oxygenSaturation = oxygenSaturation;
        this.glucose = glucose;
        this.recordedAt = recordedAt;
    }

    // Getters and setters (if needed)
    public int getVitalId() {
        return vitalId;
    }

    public int getPatientId() {
        return patientId;
    }

    public double getBpSystolic() {
        return bpSystolic;
    }

    public double getBpDiastolic() {
        return bpDiastolic;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getOxygenSaturation() {
        return oxygenSaturation;
    }

    public double getGlucose() {
        return glucose;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }
}
