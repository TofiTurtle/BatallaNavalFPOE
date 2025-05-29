package com.example.batallanavalfpoe;

import com.example.batallanavalfpoe.view.WelcomeStage;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        new WelcomeStage();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
