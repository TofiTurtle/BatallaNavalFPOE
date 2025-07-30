package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.view.WelcomeStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the game rules screen.
 * Handles navigation back to the welcome screen.
 */
public class RulesController {
    /** Base font used throughout the application */
    Font baseFont = Font.loadFont(getClass().getResourceAsStream("/com/example/batallanavalfpoe/fonts/Strjmono.ttf"), 25);

    /**
     * Handles the action to return to the welcome screen.
     *
     * @param event The action event that triggered this method
     * @throws IOException If there's an error loading the welcome stage
     */
    @FXML
    private void goToWelcome(ActionEvent event) throws IOException {
        WelcomeStage welcomeStage = new WelcomeStage();
        welcomeStage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}