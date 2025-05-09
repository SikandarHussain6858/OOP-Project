package com.example.rpms.model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {
    private String specialization;
    private String phoneNumber;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String qualification;
    private int yearsOfExperience;
    private String availableTimeSlot;
    private List<String> availableDays;

    // Full Constructor
    public Doctor(String id, String name, String email, String specialization, String phoneNumber, 
                 String gender, LocalDate dateOfBirth, String address, String qualification, 
                 int yearsOfExperience, String availableTimeSlot, List<String> availableDays) {
        super(id, name, email);
        this.specialization = specialization;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.qualification = qualification;
        this.yearsOfExperience = yearsOfExperience;
        this.availableTimeSlot = availableTimeSlot;
        this.availableDays = availableDays;
    }

    // Minimal Constructor
    public Doctor(String id, String name, String email) {
        super(id, name, email);
    }

    // Default Constructor
    public Doctor() {
        super("defaultId", "defaultName", "defaultEmail");
    }

    // Getters and Setters
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getAvailableTimeSlot() { return availableTimeSlot; }
    public void setAvailableTimeSlot(String availableTimeSlot) { this.availableTimeSlot = availableTimeSlot; }

    public List<String> getAvailableDays() { return availableDays; }
    public void setAvailableDays(List<String> availableDays) { this.availableDays = availableDays; }

    // Database operations
    public boolean saveToDatabase() {
        String sql = "INSERT INTO users (username, email, role) VALUES (?, ?, 'DOCTOR')";
        String detailsSql = "INSERT INTO doctor_details (doctor_id, specialization, phone_number, " +
                           "gender, date_of_birth, address, qualification, years_of_experience, " +
                           "available_time_slot) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, getName());
                stmt.setString(2, getEmail());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int doctorId = rs.getInt(1);
                        try (PreparedStatement detailsStmt = conn.prepareStatement(detailsSql)) {
                            detailsStmt.setInt(1, doctorId);
                            detailsStmt.setString(2, specialization);
                            detailsStmt.setString(3, phoneNumber);
                            detailsStmt.setString(4, gender);
                            detailsStmt.setDate(5, Date.valueOf(dateOfBirth));
                            detailsStmt.setString(6, address);
                            detailsStmt.setString(7, qualification);
                            detailsStmt.setInt(8, yearsOfExperience);
                            detailsStmt.setString(9, availableTimeSlot);
                            
                            detailsStmt.executeUpdate();
                            conn.commit();
                            return true;
                        }
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Patient> getPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                    "JOIN appointments a ON u.user_id = a.patient_id " +
                    "WHERE a.doctor_id = ? AND u.role = 'PATIENT'";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                patients.add(new Patient(
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("username"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    public List<Appointment> getAppointments() {
        return AppointmentManager.getDoctorAppointments(Integer.parseInt(getId()));
    }

    public void approveAppointment(int appointmentId) {
        AppointmentManager.approveAppointment(appointmentId);
    }

    public void cancelAppointment(int appointmentId) {
        AppointmentManager.cancelAppointment(appointmentId);
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address='" + address + '\'' +
                ", qualification='" + qualification + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", availableTimeSlot='" + availableTimeSlot + '\'' +
                ", availableDays=" + availableDays +
                '}';
    }

    public static boolean removeDoctorByIdNameEmail(String id, String name, String email) {
        String sql = "DELETE FROM users WHERE user_id = ? AND username = ? AND email = ? AND role = 'DOCTOR'";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
