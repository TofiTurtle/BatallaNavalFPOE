package com.example.batallanavalfpoe.view;

import com.example.batallanavalfpoe.controller.OpponentController;
import com.example.batallanavalfpoe.model.GameBoard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * {@link Stage} responsible for displaying the opponent's positioning board.
 * <p>
 * Loads its UI from {@code /com/example/batallanavalfpoe/opponent-view.fxml}, sets a
 * localized window title, disables resizing, and exposes accessors to the controller
 * and the opponent's {@link GameBoard}.
 * </p>
 *
 * <p><strong>Error handling:</strong> The constructor catches {@link IOException} and
 * prints the stack trace. If loading fails, {@link #controller} and {@link #opponentBoard}
 * may remain {@code null}.</p>
 *
 * @since 1.0
 */
public class OpponentStage extends Stage {

    /** Controller associated with the loaded FXML. */
    private OpponentController controller;

    /** Opponent's board provided by the controller. */
    private GameBoard opponentBoard;

    /**
     * Creates the stage and initializes its scene from FXML.
     * <p>
     * Steps performed:
     * <ol>
     *   <li>Loads the FXML and builds a {@link Scene}.</li>
     *   <li>Sets a Spanish window title and disables resizing.</li>
     *   <li>Obtains the {@link OpponentController} and retrieves the {@link GameBoard}.</li>
     * </ol>
     * </p>
     *
     * <p><strong>Note:</strong> This constructor does not call {@code show()}. The caller
     * should decide when to present the window.</p>
     */
    public OpponentStage() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/batallanavalfpoe/opponent-view.fxml")
            );
            Scene scene = new Scene(loader.load());
            setTitle("Tablero de posición del oponente");
            setResizable(false);
            setScene(scene);

            controller = loader.getController();
            opponentBoard = controller.getGameBoard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the controller associated with the opponent view.
     *
     * @return the {@link OpponentController}, or {@code null} if loading failed
     */
    public OpponentController getController() {
        return controller;
    }

    /**
     * Returns the opponent's {@link GameBoard}.
     *
     * @return the opponent board, or {@code null} if loading failed
     */
    public GameBoard getOpponentBoard() {
        return opponentBoard;
    }
}
