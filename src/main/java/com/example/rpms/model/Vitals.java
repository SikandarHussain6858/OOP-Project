package com.example.rpms.model;

import java.time.LocalDateTime;
import javafx.beans.property.*;

public class Vitals {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty patientId = new SimpleIntegerProperty();
    private final DoubleProperty bloodPressureSystolic = new SimpleDoubleProperty();
    private final DoubleProperty bloodPressureDiastolic = new SimpleDoubleProperty();
    private final DoubleProperty heartRate = new SimpleDoubleProperty();
    private final DoubleProperty temperature = new SimpleDoubleProperty();
    private final DoubleProperty oxygenSaturation = new SimpleDoubleProperty();
    private final DoubleProperty glucose = new SimpleDoubleProperty();
    private final ObjectProperty<LocalDateTime> recordedAt = new SimpleObjectProperty<>();

    public Vitals(int id, int patientId, double bpSystolic, double bpDiastolic, 
                 double heartRate, double temperature, double oxygenSaturation, 
                 double glucose, LocalDateTime recordedAt) {
        setId(id);
        setPatientId(patientId);
        setBloodPressureSystolic(bpSystolic);
        setBloodPressureDiastolic(bpDiastolic);
        setHeartRate(heartRate);
        setTemperature(temperature);
        setOxygenSaturation(oxygenSaturation);
        setGlucose(glucose);
        setRecordedAt(recordedAt);
    }

    // Getters and setters
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    public int getPatientId() { return patientId.get(); }
    public void setPatientId(int value) { patientId.set(value); }
    public IntegerProperty patientIdProperty() { return patientId; }

    public double getBloodPressureDiastolic() { return bloodPressureDiastolic.get(); }
    public void setBloodPressureDiastolic(double value) { bloodPressureDiastolic.set(value); }
    public DoubleProperty bloodPressureDiastolicProperty() { return bloodPressureDiastolic; }

    public double getBloodPressureSystolic() { return bloodPressureSystolic.get(); }
    public void setBloodPressureSystolic(double value) { bloodPressureSystolic.set(value); }
    public DoubleProperty bloodPressureSystolicProperty() { return bloodPressureSystolic; }

    public double getTemperature() { return temperature.get(); }
    public void setTemperature(double value) { temperature.set(value); }
    public DoubleProperty temperatureProperty() { return temperature; }

    public double getHeartRate() { return heartRate.get(); }
    public void setHeartRate(double value) { heartRate.set(value); }
    public DoubleProperty heartRateProperty() { return heartRate; }

    public double getOxygenSaturation() { return oxygenSaturation.get(); }
    public void setOxygenSaturation(double value) { oxygenSaturation.set(value); }
    public DoubleProperty oxygenSaturationProperty() { return oxygenSaturation; }

    public double getGlucose() { return glucose.get(); }
    public void setGlucose(double value) { glucose.set(value); }
    public DoubleProperty glucoseProperty() { return glucose; }

    public LocalDateTime getRecordedAt() { return recordedAt.get(); }
    public void setRecordedAt(LocalDateTime value) { recordedAt.set(value); }
    public ObjectProperty<LocalDateTime> recordedAtProperty() { return recordedAt; }


}
