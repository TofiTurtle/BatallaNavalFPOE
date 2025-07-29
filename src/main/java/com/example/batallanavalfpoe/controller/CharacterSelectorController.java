package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.GameState;
import com.example.batallanavalfpoe.model.PlainTextFileHandler;
import com.example.batallanavalfpoe.model.Player;
import com.example.batallanavalfpoe.view.GameStage;
import com.example.batallanavalfpoe.view.WelcomeStage;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for character selection screen in the naval battle game.
 * Handles player character selection, name input, and game initialization.
 */
public class CharacterSelectorController {
    @FXML
    private ImageView imageView;

    @FXML
    private TextField textField;

    @FXML
    private Label emptyNameLabel;

    private List<Image> images;
    private int currentIndex = 2;
    Font baseFont = Font.loadFont(getClass().getResourceAsStream("/com/example/batallanavalfpoe/fonts/Strjmono.ttf"), 25);

    /**
     * Array containing paths to character images.
     * Needed separately from the Image list to store string paths.
     */
    private String PathListImages[] = {
            "/com/example/batallanavalfpoe/images/character1.PNG",
            "/com/example/batallanavalfpoe/images/character2.PNG",
            "/com/example/batallanavalfpoe/images/character3.PNG",
            "/com/example/batallanavalfpoe/images/character4.PNG",
            "/com/example/batallanavalfpoe/images/character5.PNG",
            "/com/example/batallanavalfpoe/images/character6.PNG",
            "/com/example/batallanavalfpoe/images/character7.PNG"
    };

    private PlainTextFileHandler plainTextFileHandler;

    /**
     * Returns to the welcome screen.
     * @param event The action event triggering this method
     * @throws IOException if there's an error loading the welcome stage
     */
    @FXML
    private void welcomeStage(ActionEvent event) throws IOException {
        WelcomeStage welcomeStage = new WelcomeStage();
        welcomeStage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller.
     * Sets up character images, text field listener, and file handler.
     */
    @FXML
    public void initialize() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                emptyNameLabel.setVisible(false);
            }
        });

        images = List.of(
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/character1.PNG")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/character2.PNG")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/character3.PNG")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/character4.PNG")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/character5.PNG")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/character6.PNG")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/character7.PNG"))
        );
        imageView.setImage(images.get(currentIndex));

        plainTextFileHandler = new PlainTextFileHandler();
    }

    /**
     * Displays the next character image in the sequence.
     * Wraps around to the first image if at the end.
     */
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

    /**
     * Displays the previous character image in the sequence.
     * Wraps around to the last image if at the beginning.
     */
    @FXML
    private void previousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            imageView.setImage(images.get(currentIndex));
        } else {
            currentIndex = images.size() - 1;
            imageView.setImage(images.get(currentIndex));
        }
    }

    /**
     * Handles the play button action.
     * Validates player name, creates player data, and starts the game.
     * @param event The action event triggering this method
     * @throws IOException if there's an error starting the game
     */
    @FXML
    private void playButton(ActionEvent event) throws IOException {
        String name = textField.getText().trim();
        if (name.isEmpty()) {
            emptyNameLabel.setVisible(true);
            return;
        }

        Player player = new Player(name, PathListImages[currentIndex]);

        String content = player.getPlayerName() + "," + player.getCharacterImagePath();
        plainTextFileHandler.writeToFile("player_data.csv", content);

        GameState gameState = null;
        // vitalToken set to 1 indicates starting a new game from scratch
        GameStage gameStage = new GameStage(images.get(currentIndex), name, gameState, 0);
        gameStage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}