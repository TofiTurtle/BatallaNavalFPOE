package com.example.batallanavalfpoe.model;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

/**
 * Defines the contract for a Battleship game board.
 * <p>
 * Implementing classes must manage ships placement,
 * shots tracking, and grid interaction.
 * </p>
 */
public interface IGameBoard {

    /**
     * Checks if a shot has been fired at the specified cell.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return {@code true} if the cell has been shot at, {@code false} otherwise
     */
    boolean getshotsOnterritory(int row, int col);

    /**
     * Marks a shot as fired at the specified cell.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    void setShotsOnterritory(int row, int col);

    /**
     * Returns the ship located at the specified cell.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return the {@link Ship} at the specified position, or {@code null} if none
     */
    Ship getShip(int row, int col);

    /**
     * Returns the matrix containing all ships on the board.
     *
     * @return a 2D array of {@link Ship} objects
     */
    Ship[][] getShips();

    /**
     * Returns the matrix that tracks all fired shots.
     *
     * @return a 2D boolean array representing shots fired
     */
    boolean[][] getShotsBoard();

    /**
     * Returns the matrix that indicates which cells are occupied by ships.
     *
     * @return a 2D boolean array representing occupied cells
     */
    boolean[][] getOccupiedCells();

    /**
     * Restores the board state using the given ships and shot data.
     *
     * @param ships            a 2D array of {@link Ship} objects
     * @param ShotsOnterritory a 2D boolean array representing shots fired
     * @param occupiedCells    a 2D boolean array representing occupied cells
     */
    void restoreBoard(Ship[][] ships, boolean[][] ShotsOnterritory, boolean[][] occupiedCells);

    /**
     * Checks if the specified coordinates are within the board boundaries.
     *
     * @param row the row index to check
     * @param col the column index to check
     * @return {@code true} if the coordinates are valid, {@code false} otherwise
     */
    boolean isWithinBounds(int row, int col);

    /**
     * Checks if the specified cell is occupied by a ship.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @return {@code true} if the cell is occupied, {@code false} otherwise
     */
    boolean isOccupied(int row, int col);

    /**
     * Marks the specified cell as occupied by a ship.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    void setOccupied(int row, int col);

    /**
     * Sets up the visual grid representation.
     *
     * @param gridPane the {@link GridPane} to configure
     */
    void setupGrid(GridPane gridPane);

    /**
     * Creates a single cell for the grid.
     *
     * @return a {@link Rectangle} representing the cell
     */
    Rectangle createCell();

    /**
     * Deactivates the entire grid, typically when the game ends.
     *
     * @param grid the {@link GridPane} to deactivate
     */
    void deactivateGrid(GridPane grid);

    /**
     * Checks if a ship can be placed at the specified position with the given size and direction.
     *
     * @param row       the starting row index
     * @param col       the starting column index
     * @param size      the size of the ship
     * @param direction the placement direction ("HORIZONTAL" or "VERTICAL")
     * @return {@code true} if the ship can be placed, {@code false} otherwise
     */
    boolean canPlaceShip(int row, int col, int size, String direction);

    /**
     * Places the given ship at the specified position with the given direction.
     *
     * @param row       the starting row index
     * @param col       the starting column index
     * @param ship      the {@link Ship} to place
     * @param direction the placement direction ("HORIZONTAL" or "VERTICAL")
     */
    void placeShip(int row, int col, Ship ship, String direction);
}
