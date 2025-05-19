package com.example.batallanavalfpoe.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class WelcomeStage extends Stage {
    public WelcomeStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/welcome-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        setTitle("Batalla Naval - Bienvenida");
        setResizable(false);
        setScene(scene);
        show();
    }
}
