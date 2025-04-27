package com.example.rpms.model;

import java.util.Date;

public class VitalSign {
    // attributes for vital signs including date for keeping track of when they were recorded
    private String patientID;
    private double heartRate;
    private double oxygenLevel;
    private double bloodPressure;
    private double temperature;
    private Date dateRecorded;

    // constructor to initialize the vital sign attributes
    public VitalSign(String patientID, double heartRate, double oxygenLevel, double bloodPressure, double temperature, Date dateRecorded) {
        this.patientID = patientID;
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.dateRecorded = dateRecorded;
    }

    // getters
    public String getUserId() { return patientID; }
    public double getHeartRate() { return heartRate;}
    public double getOxygenLevel() { return oxygenLevel;}
    public double getBloodPressure() { return bloodPressure;}
    public double getTemperature() { return temperature;}
    public Date getDateRecorded() { return dateRecorded;}

    // setters in case modification is required
    public void setHeartRate(double heartRate) { this.heartRate = heartRate; }
    public void setOxygenLevel(double oxygenLevel) { this.oxygenLevel = oxygenLevel; }
    public void setBloodPressure(double bloodPressure) { this.bloodPressure = bloodPressure; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setDateRecorded(Date dateRecorded) { this.dateRecorded = dateRecorded; }


    // overriden toString method to display the vital sign details
    @Override
    public String toString() {
        return "PatientID: " + patientID +
                "\nHeart Rate: " + heartRate + " bpm"+
                "\nOxygen Level: " + oxygenLevel + "%" +
                "\nBlood Pressure: " + bloodPressure + " mmHg" +
                "\nTemperature: " + temperature + " °C" +
                "\nDate Recorded: " + dateRecorded;
    }
}
