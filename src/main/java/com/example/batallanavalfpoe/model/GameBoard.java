package com.example.batallanavalfpoe.model;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Random;

public class GameBoard {
    protected boolean[][] occupiedCells;
    protected Ship[][] ships; //matriz de ships que almacenaran su informacion
    public int rows;
    public int cols;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.occupiedCells = new boolean[rows][cols];
        this.ships = new Ship[rows][cols]; //inicializamos nuestra matriz de ships
    }

    /*
    recibe como parametro fila y columna, si estas son menores a las del tablero devuelve true
     */
    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /*
    recordar que e tablero es un arreglo de bool entonces devuelve el bool dependiendo de la fila y columna
     */
    public boolean isOccupied(int row, int col) {
        return occupiedCells[row][col];
    }

    /*
    pone true en una celda en especifico
     */
    public void setOccupied(int row, int col) {
        occupiedCells[row][col] = true;
    }

    /*
    muestra las lineas del tablero, fija su tamaño, fija el tamaño de cada celda de 40x40
     */
    public void setupGrid(GridPane gridPane) {
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefSize(400, 400);
        for (int i = 0; i < rows; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(40));
            gridPane.getRowConstraints().add(new RowConstraints(40));
        }
    }

    /*
    crea los rectangulos que simulan las celdas
     */
    public Rectangle createCell() {
        Rectangle cell = new Rectangle(40, 40);
        cell.setFill(Color.LIGHTGRAY);
        cell.setStroke(Color.BLACK);
        return cell;
    }

    /*
    desactiva las celdas
     */
    public void deactivateGrid(GridPane grid) {
        for (var node : grid.getChildren()) {
            if (node instanceof Rectangle) {
                node.setDisable(true);
            }
        }
    }

    /*
    Valida si se puede colocar un barco en el tablero, según dirección y tamaño
     */
    public boolean canPlaceShip(int row, int col, int size, String direction) {
        int dRow = 0, dCol = 0;
        switch (direction) {
            case "UP" -> dRow = 1;
            case "DOWN" -> dRow = 1;
            case "LEFT" -> dCol = 1;
            case "RIGHT" -> dCol = 1;
        }

        for (int i = 0; i < size; i++) {
            int r = row + dRow * i;
            int c = col + dCol * i;

            if (!isWithinBounds(r, c) || isOccupied(r, c)) {
                return false;
            }
        }
        return true;
    }

    /*
     Coloca el barco en el tablero actualizando las celdas ocupadas
     */
    public void placeShip(int row, int col,Ship ship, String direction) {
        int dRow = 0, dCol = 0;

        switch (direction) {
            case "UP" -> dRow = 1;
            case "DOWN" -> dRow = 1;
            case "LEFT" -> dCol = 1;
            case "RIGHT" -> dCol = 1;
        }

        for (int i = 0; i < ship.getSize(); i++) { //podemos cambiar el size por el getsize()
            int r = row + dRow * i;
            int c = col + dCol * i;
            setOccupied(r, c);
            ships[r][c] = ship;
        }
    }

}
