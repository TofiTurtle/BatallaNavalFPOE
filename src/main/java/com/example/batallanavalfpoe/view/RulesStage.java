package com.example.batallanavalfpoe.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RulesStage extends Stage {
    public RulesStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/rules-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        setTitle("REGLAS / INSTRUCCIONES DEL JUEGO BATALLA NAVAL !!!");
        setResizable(false);
        setScene(scene);
        show();
    }
}
