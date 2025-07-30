package com.example.batallanavalfpoe.model;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

/**
 * Default adapter implementation of the {@link IGameBoard} interface.
 * <p>
 * All methods are provided with empty or default behavior.
 * This class can be extended to override only the needed methods.
 * </p>
 */
public class GameBoardAdapter implements IGameBoard {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getshotsOnterritory(int row, int col) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShotsOnterritory(int row, int col) {
        // No implementation by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ship getShip(int row, int col) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Ship[][] getShips() {
        return new Ship[0][0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean[][] getShotsBoard() {
        return new boolean[0][0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean[][] getOccupiedCells() {
        return new boolean[0][0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restoreBoard(Ship[][] ships, boolean[][] ShotsOnterritory, boolean[][] occupiedCells) {
        // No implementation by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWithinBounds(int row, int col) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOccupied(int row, int col) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOccupied(int row, int col) {
        // No implementation by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setupGrid(GridPane gridPane) {
        // No implementation by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle createCell() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateGrid(GridPane grid) {
        // No implementation by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPlaceShip(int row, int col, int size, String direction) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void placeShip(int row, int col, Ship ship, String direction) {
        // No implementation by default
    }

}
