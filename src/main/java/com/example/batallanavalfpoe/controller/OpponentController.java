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

    private final List<Ship> fleet = new ArrayList<>();

    public GameBoard getGameBoard() {
        return opponentBoard;
    }


    /*
    segun tengo enetendido valeria crea shipplacement (una clase interna) que contiene la dirrecion y la columna y placedship
    la cual tiene un shipplacement y un ship (contiene clase y nombre)
     */
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
        se llama a la funcion de addFleet la cual crea objetos de la clase ship
         */
        addFleet(4, "Portaviones", 1);
        addFleet(3, "Submarino", 2);
        addFleet(2, "Destructor", 3);
        addFleet(1, "Fragata", 4);

        opponentBoard = new GameBoard(BOARD_ROWS, BOARD_COLS);
        opponentBoard.setupGrid(opponentGrid);

        /*
        crea los rectangulos que simulan las celdas, muestras la cuadricula y define el tamaño de las celdas 40x40
         */
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                opponentGrid.add(opponentBoard.createCell(), col, row);
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

    /*
    recibe el tamaño, el nombre y la cantidad de barcos
    y ademas, se agregan estos barcos a la lista
     */
    private void addFleet(int size, String name, int count) {
        for (int i = 0; i < count; i++) {
            fleet.add(new Ship(size, name));
        }
    }

    /*
    devuelve una lista de placedships, hace un for el cual recorre el arreglo de barcos por cada barco y
    realiza un while que genera numeros de columna y filas al azar, a demas se crea un arreglo con las dirreciones
    y se llama al opponentboard con la funcion de canplace con el numero de columna y fila al azar, su tamaño y dirrecion
     */
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

    /*
    recibe como parametro el arreglo de los barcos con la fila, comlumna, dirrecion, tamaño y tipo de barco y los genera en el grid pane
    dependiendo de esos factores
     */
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
