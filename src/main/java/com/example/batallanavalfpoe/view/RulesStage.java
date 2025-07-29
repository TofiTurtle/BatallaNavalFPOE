package com.example.batallanavalfpoe.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Dedicated {@link Stage} for displaying the game's rules and instructions.
 * <p>
 * This stage loads its UI from {@code /com/example/batallanavalfpoe/rules-view.fxml},
 * sets a localized window title, applies a non-resizable scene, and shows itself.
 * </p>
 *
 * @since 1.0
 */
public class RulesStage extends Stage {

    /**
     * Creates and immediately shows the rules/instructions window.
     * <p>
     * Steps performed:
     * <ol>
     *   <li>Loads the FXML via {@link FXMLLoader}.</li>
     *   <li>Builds a {@link Scene} from the loaded hierarchy.</li>
     *   <li>Sets a Spanish window title required by the UI.</li>
     *   <li>Disables resizing and shows the stage.</li>
     * </ol>
     * </p>
     *
     * @throws IOException if the FXML resource cannot be found or loaded
     */
    public RulesStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/batallanavalfpoe/rules-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        setTitle("REGLAS / INSTRUCCIONES DEL JUEGO BATALLA NAVAL !!!");
        setResizable(false);
        setScene(scene);
        show();
    }
}
