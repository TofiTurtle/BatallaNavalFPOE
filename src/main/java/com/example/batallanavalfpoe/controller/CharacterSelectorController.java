package com.example.batallanavalfpoe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharacterSelectorController {
    @FXML
    private Button jugarButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button previousButton, nextButton;

    private List<Image> images;
    private int currentIndex = 2;

    @FXML
        private void welcomeStage (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/welcome-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML
    public void initialize() {
        images = List.of(
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/imagenes/personajeUno.jpg")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/imagenes/personajeDos.jpg")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/imagenes/personajeTres.jpg"))
        );
        imageView.setImage(images.get(currentIndex));
    }


    @FXML
    private void nextImage() {
        if (currentIndex < images.size() - 1) {
            currentIndex++;
            imageView.setImage(images.get(currentIndex));
        } else {
            currentIndex = 0;
            imageView.setImage(images.get(currentIndex));
        }
    }

    @FXML
    private void previousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            imageView.setImage(images.get(currentIndex));


        }else {
            currentIndex = 2;
            imageView.setImage(images.get(currentIndex));

        }
    }
    @FXML
    private void playButton (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/game-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    Image getImage() {
        return images.get(currentIndex);
    }





}
