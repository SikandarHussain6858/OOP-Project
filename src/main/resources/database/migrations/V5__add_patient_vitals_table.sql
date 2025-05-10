CREATE TABLE IF NOT EXISTS patient_vitals (
    vital_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    heart_rate INT,
    oxygen_saturation INT,
    bp_systolic INT,
    bp_diastolic INT,
    temperature DECIMAL(4,2),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);
