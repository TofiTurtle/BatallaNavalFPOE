package com.example.batallanavalfpoe.model;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameBoard {
    protected boolean[][] occupiedCells;
    public int rows;
    public int cols;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.occupiedCells = new boolean[rows][cols];
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean isOccupied(int row, int col) {
        return occupiedCells[row][col];
    }

    public void setOccupied(int row, int col) {
        occupiedCells[row][col] = true;
    }

    public void setupGrid(GridPane gridPane) {
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefSize(400, 400);
        for (int i = 0; i < rows; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(40));
            gridPane.getRowConstraints().add(new RowConstraints(40));
        }
    }

    public Rectangle createCell(int row, int col) {
        Rectangle cell = new Rectangle(40, 40);
        cell.setFill(Color.LIGHTGRAY);
        cell.setStroke(Color.BLACK);
        return cell;
    }

    public void deactivateGrid(GridPane grid) {
        for (var node : grid.getChildren()) {
            if (node instanceof Rectangle) {
                node.setDisable(true);
            }
        }
    }

    // Valida si se puede colocar un barco en el tablero, según dirección y tamaño
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

    // Coloca el barco en el tablero actualizando las celdas ocupadas
    public void placeShip(int row, int col, int size, String direction) {
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
            setOccupied(r, c);
        }
    }
}
