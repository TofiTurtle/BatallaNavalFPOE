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
    private static List<PlacedShip> savedPlacedShips = null;

    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;

    private GameBoard opponentBoard;

    public GameBoard getGameBoard() {
        return opponentBoard;
    }

    public static class ShipPlacement {
        public String direction;
        int row, col;

        ShipPlacement(int row, int col, String direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }
    }

    static class PlacedShip {
        ShipPlacement placement;
        Ship ship;

        PlacedShip(ShipPlacement placement, Ship ship) {
            this.placement = placement;
            this.ship = ship;
        }
    }

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
                opponentGrid.add(opponentBoard.createCell(BOARD_ROWS, BOARD_COLS), col, row);
            }
        }

        if (savedPlacedShips != null) {
            for (PlacedShip ps : savedPlacedShips) {
                opponentBoard.placeShip(ps.placement.row, ps.placement.col, ps.ship.getSize(), ps.placement.direction);
            }
            renderPlacedShips(savedPlacedShips);
        } else {
            savedPlacedShips = placeAllShipsRandomly();
        }
    }

    private void addFleet(int size, String name, int count) {
        for (int i = 0; i < count; i++) {
            fleet.add(new Ship(size, name));
        }
    }

    private List<PlacedShip> placeAllShipsRandomly() {
        List<PlacedShip> placedShips = new ArrayList<>();
        Random random = new Random();

        for (Ship ship : fleet) {
            boolean placed = false;

            while (!placed) {
                int row = random.nextInt(BOARD_ROWS);
                int col = random.nextInt(BOARD_COLS);
                String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
                String direction = directions[random.nextInt(directions.length)];

                if (opponentBoard.canPlaceShip(row, col, ship.getSize(), direction)) {
                    opponentBoard.placeShip(row, col, ship.getSize(), direction);
                    placedShips.add(new PlacedShip(new ShipPlacement(row, col, direction), ship));
                    placed = true;
                }
            }
        }

        renderPlacedShips(placedShips);
        return placedShips;
    }

    public static List<PlacedShip> getSavedPlacedShips() {
        return savedPlacedShips;
    }

    private void renderPlacedShips(List<PlacedShip> placedShips) {
        double cellSize = 40;

        for (PlacedShip ps : placedShips) {
            double width = cellSize;
            double height = cellSize;

            boolean vertical = ps.placement.direction.equals("UP") || ps.placement.direction.equals("DOWN");

            if (vertical) {
                height = ps.ship.getSize() * cellSize;
            } else {
                width = ps.ship.getSize() * cellSize;
            }

            Rectangle rect = new Rectangle(width, height);

            // para rotar la imagen junto con el recangulo
            String imageName = switch (ps.ship.getSize()) {
                case 1 -> "frigate";
                case 2 -> "destroyer";
                case 3 -> "submarine";
                case 4 -> "carrier";
                default -> "default";
            };

            String path = switch (ps.placement.direction) {
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

            opponentGrid.add(rect, ps.placement.col, ps.placement.row);

            if (vertical) {
                GridPane.setRowSpan(rect, ps.ship.getSize());
            } else {
                GridPane.setColumnSpan(rect, ps.ship.getSize());
            }
        }
    }
}
