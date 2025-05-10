module com.example.rpms {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires jakarta.mail;    // Changed from java.mail
    requires java.desktop;
    requires java.sql;
    requires jdk.jdi;
    requires mysql.connector.j;    // Changed from com.mysql.j
    requires transitive javafx.graphics;

    opens com.example.rpms to javafx.fxml;
    opens com.example.rpms.controller to javafx.fxml;
    opens com.example.rpms.model to javafx.base;

    exports com.example.rpms;
    exports com.example.rpms.controller;
    exports com.example.rpms.model;
}
