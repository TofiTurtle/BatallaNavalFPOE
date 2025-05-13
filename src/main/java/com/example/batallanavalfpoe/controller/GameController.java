package com.example.batallanavalfpoe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

import java.io.IOException;


public class GameController {
    @FXML
    private ImageView img;

    private CharacterSelectorController characterController;

    @FXML
    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/characterSelector-view.fxml"));
        Parent root = loader.load();
        characterController = loader.getController();

        img.setImage(characterController.getImage());
    }

}
