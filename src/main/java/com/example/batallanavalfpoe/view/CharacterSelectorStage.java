package com.example.batallanavalfpoe.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class CharacterSelectorStage extends Stage {
    public CharacterSelectorStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/characterSelector-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        setTitle("Batalla Naval");
        setResizable(false);
        setScene(scene);
        show();
    }
}
