package com.example.rpms.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Read SQL file content
            String sql = new BufferedReader(
                new InputStreamReader(
                    DatabaseInitializer.class.getResourceAsStream("/database/schema.sql")))
                        .lines()
                        .collect(Collectors.joining("\n"));

            // Split and execute each SQL statement
            for (String statement : sql.split(";")) {
                if (!statement.trim().isEmpty()) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(statement);
                    }
                }
            }
            System.out.println("Database schema initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}