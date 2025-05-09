package com.example.rpms;

import com.example.rpms.model.DatabaseConnector;
import com.example.rpms.model.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database schema
        DatabaseInitializer.initializeDatabase();
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/rpms/fxml/login.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("RPMS");  // Add a title
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}