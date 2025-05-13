package com.example.batallanavalfpoe.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class GameController {
    @FXML
    private ImageView img;

    @FXML
    public void setCharacterImage(Image image) {
        img.setImage(image);
    }
}
