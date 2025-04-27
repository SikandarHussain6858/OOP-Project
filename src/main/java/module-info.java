module com.example.rpms {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx; // keep if you are using formsfx, else remove

    opens com.example.rpms to javafx.fxml;
    opens com.example.rpms.controller to javafx.fxml;

    exports com.example.rpms;
    exports com.example.rpms.controller;
}
