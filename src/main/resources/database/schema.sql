CREATE DATABASE IF NOT EXISTS rpms;
USE rpms;

-- Users table for all types of users
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- Added password field
    email VARCHAR(100) UNIQUE NOT NULL,
    role ENUM('ADMIN', 'DOCTOR', 'PATIENT') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Doctor details table
CREATE TABLE IF NOT EXISTS doctor_details (
    doctor_id INT PRIMARY KEY,
    specialization VARCHAR(100),
    phone_number VARCHAR(20),
    gender VARCHAR(10),
    date_of_birth DATE,
    address TEXT,
    qualification VARCHAR(200),
    years_of_experience INT,
    available_time_slot VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Patient details table
CREATE TABLE IF NOT EXISTS patient_details (
    patient_id INT PRIMARY KEY,
    dob DATE,
    gender VARCHAR(10),
    contact VARCHAR(20),
    address TEXT,
    medical_history TEXT,
    emergency_contact VARCHAR(20),  -- Added emergency contact
    blood_group VARCHAR(5),        -- Added blood group
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    appointment_date DATETIME,
    status ENUM('PENDING', 'APPROVED', 'CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Emergency alerts table
CREATE TABLE IF NOT EXISTS emergency_alerts (
    alert_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    alert_type VARCHAR(50),
    alert_message TEXT,
    status ENUM('ACTIVE', 'RESOLVED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);
-- Create document management table
CREATE TABLE patient_documents (
    document_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    document_type VARCHAR(50),
    document_date DATE,
    description TEXT,
    file_path VARCHAR(255),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);

-- Create online consultation requests table
CREATE TABLE consultation_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    request_date DATE,
    reason TEXT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS patient_vitals (
    vital_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    bp_systolic DOUBLE,
    bp_diastolic DOUBLE,
    heart_rate DOUBLE,
    temperature DOUBLE,
    oxygen_saturation DOUBLE,
    glucose DOUBLE,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);
CREATE TABLE IF NOT EXISTS patient_alerts (
    alert_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    alert_message TEXT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id)
);
cREATE TABLE IF NOT EXISTS feedbacks (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    doctor_id INT,
    feedback TEXT,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    diagnosis TEXT NOT NULL,
    medications TEXT NOT NULL,
    instructions TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (doctor_id) REFERENCES users(user_id)
);
CREATE TABLE IF NOT EXISTS video_consultations (
    consultation_id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    consultation_date DATE NOT NULL,
    consultation_time TIME NOT NULL,
    duration VARCHAR(20) NOT NULL, -- e.g. "30 minutes", "1 hour"
    notes TEXT,
    meet_link TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS patient_doctor (
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (patient_id, doctor_id),
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);
CREATE TABLE emergency_alerts (
    id varchar(50) PRIMARY KEY,
    patient_id INT,
    message TEXT,
    status VARCHAR(20),
    severity VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_by INT,
    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (resolved_by) REFERENCES users(user_id)
);
use rpms;

CREATE TABLE emergency_alerts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT,
    type VARCHAR(50),
    message TEXT,
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_by INT,
    FOREIGN KEY (patient_id) REFERENCES users(user_id),
    FOREIGN KEY (resolved_by) REFERENCES users(user_id)
);
