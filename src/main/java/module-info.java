module com.example.batallanavalfpoe {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.batallanavalfpoe to javafx.fxml;
    exports com.example.batallanavalfpoe;
}