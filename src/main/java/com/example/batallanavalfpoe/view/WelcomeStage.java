package com.example.batallanavalfpoe.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Welcome window {@link Stage} that loads and displays the initial screen
 * of the application.
 * <p>
 * The UI is defined in {@code /com/example/batallanavalfpoe/welcome-view.fxml}.
 * This stage sets a localized window title, applies a non-resizable scene,
 * and shows itself upon construction.
 * </p>
 *
 * @since 1.0
 */
public class WelcomeStage extends Stage {

    /**
     * Creates and immediately shows the welcome window.
     * <p>
     * Steps performed:
     * <ol>
     *   <li>Loads the FXML via {@link FXMLLoader}.</li>
     *   <li>Builds a {@link Scene} from the loaded hierarchy.</li>
     *   <li>Sets the Spanish window title required by the UI.</li>
     *   <li>Disables resizing and shows the stage.</li>
     * </ol>
     * </p>
     *
     * @throws IOException if the FXML resource cannot be found or loaded
     */
    public WelcomeStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/batallanavalfpoe/welcome-view.fxml")
        );
        Scene scene = new Scene(fxmlLoader.load());
        setTitle("Batalla Naval - Bienvenida");
        setResizable(false);
        setScene(scene);
        show();
    }
}
