package com.example.batallanavalfpoe.view;

import com.example.batallanavalfpoe.controller.OpponentController;
import com.example.batallanavalfpoe.model.GameBoard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class OpponentStage extends Stage {

    private OpponentController controller;
    private GameBoard opponentBoard;

    public OpponentStage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/opponent-view.fxml"));
            Scene scene = new Scene(loader.load());
            setTitle("Tablero de posición del oponente");
            setResizable(false);
            setScene(scene);

            controller = loader.getController();
            opponentBoard = controller.getGameBoard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OpponentController getController() {
        return controller;
    }

    public GameBoard getOpponentBoard() {
        return opponentBoard;
    }
}
