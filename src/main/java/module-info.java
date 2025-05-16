module com.example.batallanavalfpoe {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.batallanavalfpoe to javafx.graphics;
    opens com.example.batallanavalfpoe.controller to javafx.fxml;
    exports com.example.batallanavalfpoe;
    exports com.example.batallanavalfpoe.controller;
}