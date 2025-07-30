package com.example.batallanavalfpoe.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import com.example.batallanavalfpoe.model.GameBoard;
import com.example.batallanavalfpoe.model.Ship;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

/**
 * Controller class for the opponent's game board in the naval battle game.
 * Handles ship placement and rendering for the opponent's fleet.
 */
public class OpponentController implements Initializable {
    Font baseFont = Font.loadFont(getClass().getResourceAsStream("/com/example/batallanavalfpoe/fonts/Strjmono.ttf"), 25);

    @FXML
    private GridPane opponentGrid;

    /** List of ships that have been placed on the board */
    private static List<Ship> savedShips = null;

    /** Constants for board dimensions */
    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;

    /** Game board model for the opponent */
    private GameBoard opponentBoard;

    /** List containing all ships in the opponent's fleet */
    private final List<Ship> fleet = new ArrayList<>();

    /**
     * Flag to control whether ships are restored from a saved game
     * or generated randomly
     */
    private static boolean isRestoredFromSavedGame = false;

    /**
     * Sets the flag for restoring from saved game
     * @param restored true if restoring from saved game, false otherwise
     */
    public static void setRestoredFromSavedGame(boolean restored) {
        isRestoredFromSavedGame = restored;
    }

    /**
     * Helper class for fleet creation and management
     */
    private static class FleetFactory {
        /**
         * Adds ships to the fleet
         * @param fleet The fleet to add ships to
         * @param size The size of the ships to add
         * @param name The name of the ships to add
         * @param count The number of ships to add
         */
        public static void addFleet(List<Ship> fleet, int size, String name, int count) {
            for (int i = 0; i < count; i++) {
                fleet.add(new Ship(size, name, 0, "default"));
            }
        }
    }

    /**
     * Initializes the controller and sets up the opponent's game board
     * @param url The location used to resolve relative paths
     * @param resourceBundle The resources used to localize the root object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create the opponent's fleet
        FleetFactory.addFleet(fleet, 4, "Portaviones", 1);
        FleetFactory.addFleet(fleet, 3, "Submarino", 2);
        FleetFactory.addFleet(fleet, 2, "Destructor", 3);
        FleetFactory.addFleet(fleet, 1, "Fragata", 4);

        opponentBoard = new GameBoard(BOARD_ROWS, BOARD_COLS);
        opponentBoard.setupGrid(opponentGrid);

        // Initialize the grid with cells
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                opponentGrid.add(opponentBoard.createCell(), col, row);
            }
        }

        // Handle ship placement based on game state
        if (isRestoredFromSavedGame == false) {
            if (savedShips != null) {
                for (Ship ship : savedShips) {
                    opponentBoard.placeShip(ship.getRow(), ship.getCol(), ship, ship.getDirection());
                }
                renderPlacedShips(savedShips);
            } else {
                savedShips = placeAllShipsRandomly();
            }
        } else {
            System.out.println("perrita");
        }
    }

    /**
     * Places all ships randomly on the board
     * @return List of placed ships
     */
    private List<Ship> placeAllShipsRandomly() {
        List<Ship> placedShips = new ArrayList<>();
        Random random = new Random();

        for (Ship ship : fleet) {
            boolean placed = false;

            while (!placed) {
                int row = random.nextInt(BOARD_ROWS);
                int col = random.nextInt(BOARD_COLS);
                String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
                String direction = directions[random.nextInt(directions.length)];

                if (opponentBoard.canPlaceShip(row, col, ship.getSize(), direction)) {
                    opponentBoard.placeShip(row, col, ship, direction);
                    ship.setPlacement(row, col, direction);
                    placedShips.add(ship);
                    placed = true;
                }
            }
        }
        renderPlacedShips(placedShips);
        return placedShips;
    }

    /**
     * Gets the list of placed ships
     * @return List of placed ships
     */
    public static List<Ship> getSavedPlacedShips() {
        return savedShips;
    }

    /**
     * Renders the placed ships on the grid
     * @param ships List of ships to render
     */
    private void renderPlacedShips(List<Ship> ships) {
        double cellSize = 40;

        for (Ship ship : ships) {
            double width = cellSize;
            double height = cellSize;

            boolean vertical = ship.getDirection().equals("UP") || ship.getDirection().equals("DOWN");

            if (vertical) {
                height = ship.getSize() * cellSize;
            } else {
                width = ship.getSize() * cellSize;
            }

            Rectangle rect = new Rectangle(width, height);

            String imageName = switch (ship.getSize()) {
                case 1 -> "frigate";
                case 2 -> "destroyer";
                case 3 -> "submarine";
                case 4 -> "carrier";
                default -> "default";
            };

            String path = switch (ship.getDirection()) {
                case "UP" -> "/com/example/batallanavalfpoe/images/" + imageName + "_up.png";
                case "DOWN" -> "/com/example/batallanavalfpoe/images/" + imageName + "_down.png";
                case "LEFT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_left.png";
                case "RIGHT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_right.png";
                default -> "/com/example/batallanavalfpoe/images/default_right.png";
            };

            try {
                Image image = new Image(getClass().getResourceAsStream(path));
                ImagePattern pattern = new ImagePattern(image);
                rect.setFill(pattern);
            } catch (Exception e) {
                rect.setFill(Color.GRAY);
            }

            opponentGrid.add(rect, ship.getCol(), ship.getRow());

            if (vertical) {
                GridPane.setRowSpan(rect, ship.getSize());
            } else {
                GridPane.setColumnSpan(rect, ship.getSize());
            }
        }
    }

    /**
     * Gets the opponent's game board
     * @return The opponent's game board
     */
    public GameBoard getGameBoard() {
        return opponentBoard;
    }

    /**
     * Restores the opponent's board from saved game data
     * @param occupiedCells Matrix indicating occupied cells
     * @param shipMatrix Matrix containing ship information
     */
    public void restoreFrom(boolean[][] occupiedCells, Ship[][] shipMatrix) {
        double cellSize = 40;
        if (occupiedCells == null || shipMatrix == null) return;

        for (int row = 0; row < occupiedCells.length; row++) {
            for (int col = 0; col < occupiedCells[row].length; col++) {
                if (occupiedCells[row][col] && shipMatrix[row][col] != null) {
                    double width = cellSize;
                    double height = cellSize;
                    boolean vertical = shipMatrix[row][col].getDirection().equals("UP") ||
                            shipMatrix[row][col].getDirection().equals("DOWN");

                    if (vertical) {
                        height = shipMatrix[row][col].getSize() * cellSize;
                    } else {
                        width = shipMatrix[row][col].getSize() * cellSize;
                    }
                    Rectangle rect = new Rectangle(width, height);
                    String imageName = switch (shipMatrix[row][col].getSize()) {
                        case 1 -> "frigate";
                        case 2 -> "destroyer";
                        case 3 -> "submarine";
                        case 4 -> "carrier";
                        default -> "default";
                    };

                    String path = switch (shipMatrix[row][col].getDirection()) {
                        case "UP" -> "/com/example/batallanavalfpoe/images/" + imageName + "_up.png";
                        case "DOWN" -> "/com/example/batallanavalfpoe/images/" + imageName + "_down.png";
                        case "LEFT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_left.png";
                        case "RIGHT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_right.png";
                        default -> "/com/example/batallanavalfpoe/images/default_right.png";
                    };

                    try {
                        Image image = new Image(getClass().getResourceAsStream(path));
                        ImagePattern pattern = new ImagePattern(image);
                        rect.setFill(pattern);
                    } catch (Exception e) {
                        rect.setFill(Color.GRAY);
                    }

                    opponentGrid.add(rect, shipMatrix[row][col].getCol(), shipMatrix[row][col].getRow());

                    if (vertical) {
                        GridPane.setRowSpan(rect, shipMatrix[row][col].getSize());
                    } else {
                        GridPane.setColumnSpan(rect, shipMatrix[row][col].getSize());
                    }
                }
            }
        }
    }
}