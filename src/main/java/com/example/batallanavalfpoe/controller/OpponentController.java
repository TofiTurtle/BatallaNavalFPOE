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

    private static List<Ship> savedShips = null;

    private static final int BOARD_ROWS = 10;
    private static final int BOARD_COLS = 10;

    private GameBoard opponentBoard;

    private final List<Ship> fleet = new ArrayList<>();

    public GameBoard getGameBoard() {
        return opponentBoard;
    }

    /* elimine las dos clases que habian antes (placedship y shipplacement) y pase todo a una sola clase interna q es
    ship, las cosas q recibian los constructores de las otras clasea aqui son solo metodos q se llaman cuando se
    necesitan y ya, pero se cumple exactamente la misma funcion
     */
    public class Ship {
        private int size;
        private String name;
        private int row;
        private int col;
        private String direction;

        public Ship(int size, String name) {
            this.size = size;
            this.name = name;
        }

        public int getSize() {
            return size;
        }

        public void setPlacement(int row, int col, String direction) {
            this.row = row;
            this.col = col;
            this.direction = direction;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public String getDirection() {
            return direction;
        }

        @Override
        public String toString() {
            return "Ship{" + "size=" + size + ", name='" + name + '\'' +
                    ", row=" + row + ", col=" + col + ", direction='" + direction + '\'' + '}';
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
        /* si hay barcos guardados anterirmente (savedships), los pone en el opponentboard, esto se hace para
        q no cambien la posicion de los barcos cada vez q se abre el opponentboard q era lo q pasaba antes
         */
        if (savedShips != null) {
            for (Ship ship : savedShips) {
                opponentBoard.placeShip(ship.getRow(), ship.getCol(), ship.getSize(), ship.getDirection());
            }
            renderPlacedShips(savedShips);
        } else {
            savedShips = placeAllShipsRandomly(); /* si no hay nada en savedships
                                          (la primera vez q se abre el opponentboard) se ponen alatoriamente los barcos */
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
    devuelve una lista de ships, hace un for el cual recorre el arreglo de barcos por cada barco y
    realiza un while que genera numeros de columna y filas al azar, a demas se crea un arreglo con las dirreciones
    y se llama al opponentboard con la funcion de canplace con el numero de columna y fila al azar, su tamaño y dirrecion
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
                    opponentBoard.placeShip(row, col, ship.getSize(), direction);
                    ship.setPlacement(row, col, direction);
                    placedShips.add(ship);
                    placed = true;
                }
            }
        }
        renderPlacedShips(placedShips);
        return placedShips;
    }

    /*retorna los barcos guardados en la lista de ship*/

    public static List<Ship> getSavedPlacedShips() {
        return savedShips;
    }

    /*
    recibe como parametro el arreglo de los barcos con la fila, comlumna, dirrecion, tamaño y tipo de barco y los genera en el grid pane
    dependiendo de esos factores
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
}
