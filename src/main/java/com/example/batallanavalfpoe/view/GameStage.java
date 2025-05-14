package com.example.batallanavalfpoe.view;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class GameStage {
    private Stage stage;
    private ImageView img;

    public GameStage(Stage stage) {
        this.stage = stage;

        setupStage();
    }
    @FXML
    private void setupStage() {
        StackPane layout = new StackPane();
        Button goToWelcome = new Button("Regresar a WelcomeStage");
        //goToWelcome.setOnAction(event -> goToWelcomeStage());

        Scene scene = new Scene(layout, 400, 200);
        stage.setTitle("Main Stage");
        stage.setScene(scene);
        stage.show();
    }


    public void show() {
        stage.show();
    }
}
