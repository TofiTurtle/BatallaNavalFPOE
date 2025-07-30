package com.example.batallanavalfpoe.model;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

/**
 * Represents the game board for the Battleship game.
 * It manages ships, occupied cells, and shots taken on the territory.
 */
public class GameBoard extends GameBoardAdapter implements Serializable {

    /** Matrix indicating if a cell is occupied by a ship. */
    protected boolean[][] occupiedCells;

    /** Matrix holding the ships placed on the board. */
    protected Ship[][] ships;

    /** Number of rows of the board. */
    public int rows;

    /** Number of columns of the board. */
    public int cols;

    /** Matrix indicating if a cell has been shot. */
    protected boolean[][] ShotsOnterritory;

    /**
     * Creates a new game board with the given number of rows and columns.
     *
     * @param rows number of rows in the board
     * @param cols number of columns in the board
     */
    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.occupiedCells = new boolean[rows][cols];
        this.ships = new Ship[rows][cols];
        this.ShotsOnterritory = new boolean[rows][cols];
    }

    /**
     * Checks whether a specific cell has been shot.
     *
     * @param row row index
     * @param col column index
     * @return {@code true} if the cell has been shot, otherwise {@code false}
     */
    public boolean getshotsOnterritory(int row, int col) {
        return ShotsOnterritory[row][col];
    }

    /**
     * Marks a specific cell as shot on the player's territory.
     * <p>This matrix is updated when the opponent shoots at the player's grid.</p>
     *
     * @param row row index
     * @param col column index
     */
    public void setShotsOnterritory(int row, int col) {
        ShotsOnterritory[row][col] = true;
    }

    /**
     * Retrieves the ship located at a specific cell.
     *
     * @param row row index
     * @param col column index
     * @return the {@link Ship} at the specified position, or {@code null} if empty
     */
    public Ship getShip(int row, int col) {
        return this.ships[row][col];
    }

    /**
     * Returns the entire matrix of ships.
     *
     * @return matrix of ships
     */
    public Ship[][] getShips() {
        return ships;
    }

    /**
     * Returns the matrix that stores shot information.
     *
     * @return matrix of shots
     */
    public boolean[][] getShotsBoard() {
        return ShotsOnterritory;
    }

    /**
     * Returns the matrix that stores occupied cell information.
     *
     * @return matrix of occupied cells
     */
    public boolean[][] getOccupiedCells() {
        return occupiedCells;
    }

    /**
     * Restores the board state from the provided matrices.
     *
     * @param ships matrix of ships
     * @param ShotsOnterritory matrix of shots
     * @param occupiedCells matrix of occupied cells
     */
    public void restoreBoard(Ship[][] ships, boolean[][] ShotsOnterritory, boolean[][] occupiedCells) {
        this.ships = ships;
        this.ShotsOnterritory = ShotsOnterritory;
        this.occupiedCells = occupiedCells;
    }

    /**
     * Checks if a given cell is within the board boundaries.
     *
     * @param row row index
     * @param col column index
     * @return {@code true} if the cell is inside the board, otherwise {@code false}
     */
    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Checks if a specific cell is occupied by a ship.
     *
     * @param row row index
     * @param col column index
     * @return {@code true} if the cell is occupied, otherwise {@code false}
     */
    public boolean isOccupied(int row, int col) {
        return occupiedCells[row][col];
    }

    /**
     * Marks a specific cell as occupied.
     *
     * @param row row index
     * @param col column index
     */
    public void setOccupied(int row, int col) {
        occupiedCells[row][col] = true;
    }

    /**
     * Sets up the grid pane for the board, configuring its size and grid lines.
     *
     * @param gridPane the grid pane to configure
     */
    public void setupGrid(GridPane gridPane) {
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefSize(400, 400);
        for (int i = 0; i < rows; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(40));
            gridPane.getRowConstraints().add(new RowConstraints(40));
        }
    }

    /**
     * Creates a single cell for the grid as a rectangle.
     *
     * @return the rectangle representing the cell
     */
    public Rectangle createCell() {
        Rectangle cell = new Rectangle(40, 40);
        cell.setFill(Color.TRANSPARENT);
        cell.setStroke(Color.WHITE);
        return cell;
    }

    /**
     * Disables all cells in the grid.
     *
     * @param grid the grid to deactivate
     */
    public void deactivateGrid(GridPane grid) {
        for (var node : grid.getChildren()) {
            if (node instanceof Rectangle) {
                node.setDisable(true);
            }
        }
    }

    /**
     * Validates if a ship can be placed on the board at a specific position,
     * based on its direction and size.
     *
     * @param row starting row index
     * @param col starting column index
     * @param size size of the ship
     * @param direction placement direction ("UP", "DOWN", "LEFT", "RIGHT")
     * @return {@code true} if the ship can be placed, otherwise {@code false}
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

    /**
     * Places a ship on the board and updates the occupied cells.
     *
     * @param row starting row index
     * @param col starting column index
     * @param ship the ship to be placed
     * @param direction placement direction ("UP", "DOWN", "LEFT", "RIGHT")
     */
    public void placeShip(int row, int col, Ship ship, String direction) {
        int dRow = 0, dCol = 0;

        switch (direction) {
            case "UP" -> dRow = 1;
            case "DOWN" -> dRow = 1;
            case "LEFT" -> dCol = 1;
            case "RIGHT" -> dCol = 1;
        }

        // Set initial position and direction of the ship
        ship.setRow(row);
        ship.setCol(col);
        ship.setDirection(direction);

        for (int i = 0; i < ship.getSize(); i++) {
            int r = row + dRow * i;
            int c = col + dCol * i;
            setOccupied(r, c);
            ships[r][c] = ship;
        }
    }
}
