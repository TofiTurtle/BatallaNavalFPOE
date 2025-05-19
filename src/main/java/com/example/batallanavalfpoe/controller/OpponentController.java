package com.example.batallanavalfpoe.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import com.example.batallanavalfpoe.model.GameBoard;

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

    private static class Ship {
        int size;
        String name;

        Ship(int size, String name) { // recibe el nombre del barco y el tamaño
            this.size = size;
            this.name = name;
        }
    }

    private static class ShipPlacement {
        int row, col;
        String direction;

        ShipPlacement(int row, int col, String direction) { //  recibe la fila columna y posicion del barco
            this.row = row;
            this.col = col;
            this.direction = direction;
        }
    }

    private static class PlacedShip {
        ShipPlacement placement; // placement indica donde y en que direccion esta colocado el barco
        Ship ship; // indica el tamaño del barco y el nombre, esdecir esta clase une ship y shipplacement

        PlacedShip(ShipPlacement placement, Ship ship) {
            this.placement = placement;
            this.ship = ship;
        }
    }

    // Lista expandida con todas las flotas a colocar
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
            // Ya fueron colocados antes
            for (PlacedShip ps : savedPlacedShips) {
                opponentBoard.placeShip(ps.placement.row, ps.placement.col, ps.ship.size, ps.placement.direction);
            }
            renderPlacedShips(savedPlacedShips);
        } else {
            // Primera vez → colocamos y guardamos
            savedPlacedShips = placeAllShipsRandomly();
        }
    }

    private void addFleet(int size, String name, int count) {
        for (int i = 0; i < count; i++) {
            fleet.add(new Ship(size, name));
        }
    }

    // Coloca todos los barcos de forma aleatoria
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

                if (opponentBoard.canPlaceShip(row, col, ship.size, direction)) {
                    opponentBoard.placeShip(row, col, ship.size, direction);
                    placedShips.add(new PlacedShip(new ShipPlacement(row, col, direction), ship));
                    placed = true;
                }
            }
        }

        renderPlacedShips(placedShips);
        return placedShips;
    }

    // Renderiza los barcos colocados con las imágenes y orientación adecuada
    private void renderPlacedShips(List<PlacedShip> placedShips) {
        double cellSize = 40;

        for (PlacedShip ps : placedShips) {
            double width = cellSize;
            double height = cellSize;

            boolean vertical = ps.placement.direction.equals("UP") || ps.placement.direction.equals("DOWN");

            if (vertical) {
                height = ps.ship.size * cellSize; // pone los barquitos
            } else {
                width = ps.ship.size * cellSize;
            }

            Rectangle rect = new Rectangle(width, height); // en este proyecto entendi la necesidad de la mailicia indigena

            ImagePattern pattern = switch (ps.ship.size) {
                case 1 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/fragata.png")));
                case 2 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/destructor.png")));
                case 3 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/submarino.png")));
                case 4 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/portaviones.png")));
                default -> null;
            };

            if (pattern != null) {
                rect.setFill(pattern);
            } else {
                rect.setFill(Color.GRAY);
            }

            // Corrige orientación para LEFT y DOWN (voltea la imagen)
            switch (ps.placement.direction) {
                case "LEFT" -> rect.setScaleX(-1);
                case "DOWN" -> rect.setScaleY(-1);
            }

            opponentGrid.add(rect, ps.placement.col, ps.placement.row);

            if (vertical) {
                GridPane.setRowSpan(rect, ps.ship.size);
            } else {
                GridPane.setColumnSpan(rect, ps.ship.size);
            }
        }
    }
}