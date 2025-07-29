package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.GameState;
import com.example.batallanavalfpoe.model.PlainTextFileHandler;
import com.example.batallanavalfpoe.model.Player;
import com.example.batallanavalfpoe.model.SerializableFileHandler;
import com.example.batallanavalfpoe.view.CharacterSelectorStage;
import com.example.batallanavalfpoe.view.GameStage;
import com.example.batallanavalfpoe.view.RulesStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the welcome screen.
 * Handles navigation to character selection, game continuation, and rules screens.
 */
public class WelcomeController {
    /** Handler for plain text file operations */
    private PlainTextFileHandler plainTextFileHandler = new PlainTextFileHandler();

    /** Handler for game state serialization/deserialization */
    private SerializableFileHandler serializableFileHandler = new SerializableFileHandler();

    /** Base font used throughout the application */
    Font baseFont = Font.loadFont(getClass().getResourceAsStream("/com/example/batallanavalfpoe/fonts/Strjmono.ttf"), 25);

    /**
     * Handles navigation to the character selection screen.
     *
     * @param event The action event that triggered this method
     * @throws IOException If there's an error loading the character selection stage
     */
    @FXML
    private void goToCharacterSelector(ActionEvent event) throws IOException {
        CharacterSelectorStage characterstage = new CharacterSelectorStage();
        characterstage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Handles continuation of a saved game.
     * Loads player data and game state from files and starts the game.
     *
     * @param event The action event that triggered this method
     * @throws IOException If there's an error loading the game data or starting the game
     */
    @FXML
    private void handleClickContinue(ActionEvent event) throws IOException {
        String[] data = plainTextFileHandler.readFromFile("player_data.csv");
        String playerName = data[0]; // Player name
        String characterImagePath = data[1]; // Character image path

        Player player = new Player(playerName, characterImagePath);
        System.out.println(playerName + "  ,  " + characterImagePath);

        // Load saved game state
        GameState gameState = (GameState) serializableFileHandler.deserialize("game_data.ser");
        // vitalToken = 1 indicates continuing an existing game
        GameStage gameStage = new GameStage(new Image(getClass().getResourceAsStream(characterImagePath)),
                playerName, gameState, 1);
        gameStage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Handles navigation to the rules screen.
     *
     * @param event The action event that triggered this method
     * @throws IOException If there's an error loading the rules stage
     */
    @FXML
    private void goToRules(ActionEvent event) throws IOException {
        RulesStage rulesstage = new RulesStage();
        rulesstage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}