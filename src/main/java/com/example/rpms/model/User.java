package com.example.rpms.model;

import java.sql.*;

public class User {
    // basic data fields that all users will possess
    private final String id;
    private final String name;
    private String email;

    // constructor
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // static method to load a user from the database
    public static User loadFromDatabase(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("username"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // method to update email in the database
    public void updateEmail(String newEmail) {
        String sql = "UPDATE users SET email = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newEmail);
            stmt.setString(2, id);
            if (stmt.executeUpdate() > 0) {
                this.email = newEmail;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // setters
    // no setter for id and name because it should not be changed once created
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Email: " + email;
    }
}