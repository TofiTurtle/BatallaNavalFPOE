package com.example.batallanavalfpoe.view;

import com.example.batallanavalfpoe.controller.GameController;
import com.example.batallanavalfpoe.model.GameState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class GameStage extends Stage {

    public GameStage(Image CurrentImage, String CurrentName, GameState gameState, int vitalToken) throws IOException {

        javafx.fxml.FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Obtener el controlador y establecer la imagen
        GameController gameController = fxmlLoader.getController();
        gameController.setCharacterImage(CurrentImage);
        gameController.setNameLabel(CurrentName);
        gameController.getGameState(gameState);

        setTitle("Batalla Naval - Partida");
        setResizable(false);
        setScene(scene);
        show();

    }
}