package com.example.batallanavalfpoe.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX {@link Stage} responsible for displaying the character selection screen.
 * <p>
 * This stage loads the FXML view {@code characterSelector-view.fxml} and sets up
 * its initial properties such as window title and resizability.
 * </p>
 *
 * <p><strong>Note:</strong> This class is designed to be initialized and shown immediately.
 * The view is tailored for Spanish-speaking users, and the title reflects this.</p>
 *
 * @since 1.0
 */
public class CharacterSelectorStage extends Stage {

    /**
     * Constructs and displays the character selector window.
     *
     * @throws IOException if the FXML file cannot be loaded or is malformed
     */
    public CharacterSelectorStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/batallanavalfpoe/characterSelector-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        setTitle("Batalla Naval - Seleccion de Personaje");
        setResizable(false);
        setScene(scene);
        show();
    }
}
