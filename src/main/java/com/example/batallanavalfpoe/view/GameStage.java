package com.example.batallanavalfpoe.view;

import com.example.batallanavalfpoe.controller.GameController;
import com.example.batallanavalfpoe.model.GameState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Game window {@link Stage} that loads the main game view, wires the controller,
 * and displays the current player's identity (name and avatar).
 * <p>
 * The FXML is loaded from {@code /com/example/batallanavalfpoe/game-view.fxml}.
 * After loading, this stage configures the scene, sets a localized window title,
 * and shows itself.
 * </p>
 *
 * @since 1.0
 */
public class GameStage extends Stage {

    /**
     * Creates and shows the game window for the current match.
     * <p>
     * This constructor:
     * <ol>
     *   <li>Loads the FXML layout and builds a {@link Scene}.</li>
     *   <li>Obtains the {@link GameController} instance from the {@link FXMLLoader}.</li>
     *   <li>Injects the player image and name into the controller.</li>
     *   <li>Passes the provided {@link GameState} to the controller for restoration.</li>
     *   <li>Sets a Spanish window title, disables resizing, and shows the stage.</li>
     * </ol>
     * </p>
     *
     * @param CurrentImage the avatar image to display for the player
     * @param CurrentName  the display name to show for the player
     * @param gameState    optional saved state to restore (may be {@code null} if starting fresh)
     * @param vitalToken   reserved parameter for validation or flow control; currently unused
     * @throws IOException if the FXML resource cannot be found or loaded
     */
    public GameStage(Image CurrentImage, String CurrentName, GameState gameState, int vitalToken) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/batallanavalfpoe/game-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());

        GameController gameController = fxmlLoader.getController();
        gameController.setCharacterImage(CurrentImage);
        gameController.setNameLabel(CurrentName);
        gameController.getGameState(gameState);

        setTitle("Batalla Naval - Partida");
        setResizable(false);
        setScene(scene);
        show();
    }
}
