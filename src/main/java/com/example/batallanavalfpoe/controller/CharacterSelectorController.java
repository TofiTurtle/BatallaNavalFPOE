package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.view.GameStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CharacterSelectorController {
    @FXML
    private Button jugarButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button previousButton, nextButton;

    @FXML
    private TextField textField;

    @FXML
    private Label emptyNameLabel;

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
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                emptyNameLabel.setVisible(false); // mira si esta vacio para mostrar el mensaje de trin
            }
        });

        images = List.of(
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/personajeUno.jpg")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/personajeDos.jpg")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/personajeTres.jpg"))
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

    /*
    MUCHO CUIDADO CON ESTA IMPLEMENTACION HAY QUE TENER CUIDADO PQ HAY CIERTAS COSITAS QUE TODAVIA
    NO ENTIENDO
     */

    @FXML
    private void playButton(ActionEvent event) throws IOException {
        String name = textField.getText().trim();

        if (name.isEmpty()) {
            emptyNameLabel.setText("Ingrese un nickname");
            emptyNameLabel.setVisible(true);
            return;
        }

        //se instancia geimstage y le pasamos por parametros la imaen y nombre
        GameStage gameStage = new GameStage(images.get(currentIndex), name);
        gameStage.show();



        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); //ojo vivo a esto pq es importante para que se cierre
    }

}
