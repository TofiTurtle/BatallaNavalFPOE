package com.example.batallanavalfpoe.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import com.example.batallanavalfpoe.model.GameBoard;
import com.example.batallanavalfpoe.model.Ship;

import java.net.URL;
import java.util.*;

public class OpponentController implements Initializable {

    @FXML
    private GridPane opponentGrid;

    private static List<Ship> savedShips = null;

    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;

    private GameBoard opponentBoard;
    private final List<Ship> fleet = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addFleet(4, "Portaviones", 1);
        addFleet(3, "Submarino", 2);
        addFleet(2, "Destructor", 3);
        addFleet(1, "Fragata", 4);

        opponentBoard = new GameBoard(BOARD_ROWS, BOARD_COLS);
        opponentBoard.setupGrid(opponentGrid);

        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                opponentGrid.add(opponentBoard.createCell(), col, row);
            }
        }

        if (savedShips != null) {
            for (Ship ship : savedShips) {
                opponentBoard.placeShip(ship.getRow(), ship.getCol(), ship, ship.getDirection());
            }
            renderPlacedShips(savedShips);
        } else {
            savedShips = placeAllShipsRandomly();
        }
    }

    private void addFleet(int size, String name, int count) {
        for (int i = 0; i < count; i++) {
            fleet.add(new Ship(size, name,0));
        }
    }

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

    public static List<Ship> getSavedPlacedShips() {
        return savedShips;
    }

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

    public GameBoard getGameBoard() {
        return opponentBoard;
    }
}