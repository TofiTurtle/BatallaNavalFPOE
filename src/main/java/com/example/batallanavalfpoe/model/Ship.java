package com.example.batallanavalfpoe.model;

import java.io.Serializable;

/**
 * Represents a ship placed on the game board.
 * <p>
 * A ship has a name, size (number of contiguous cells it occupies),
 * a placement (row, column, direction), and a hit counter that increases
 * when the ship is successfully targeted.
 * </p>
 *
 * <p><strong>Coordinate system:</strong> {@code row} and {@code col} are zero-based
 * indexes relative to the board. {@code direction} is a string label
 * used by the game logic (e.g., "UP", "DOWN", "LEFT", "RIGHT").</p>
 */
public class Ship implements Serializable {

    /** Number of contiguous cells occupied by the ship. */
    private int size;

    /** Human-readable name of the ship. */
    private String name;

    /** Starting row of the ship placement (zero-based). */
    private int row;

    /** Starting column of the ship placement (zero-based). */
    private int col;

    /** Direction label of the ship placement (e.g., "UP", "DOWN", "LEFT", "RIGHT"). */
    private String direction;

    /** Number of successful hits received by this ship. */
    private int hits;

    /**
     * Creates a new {@code Ship} with the provided attributes.
     *
     * @param size       number of cells occupied by the ship
     * @param name       display name of the ship
     * @param hits       initial hit count (usually 0 at creation time)
     * @param direction  placement direction label
     */
    public Ship(int size, String name, int hits, String direction) {
        this.size = size;
        this.name = name;
        this.hits = hits;
        this.direction = direction;
    }

    // ---------------------------------------------------------------------
    // Placement mutators (used by GameBoard during placement)
    // ---------------------------------------------------------------------

    /**
     * Sets the starting row of this ship.
     *
     * @param row zero-based row index
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Sets the starting column of this ship.
     *
     * @param col zero-based column index
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Sets the placement direction label.
     *
     * @param direction direction label (e.g., "UP", "DOWN", "LEFT", "RIGHT")
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    // ---------------------------------------------------------------------
    // Getters and game-related helpers
    // ---------------------------------------------------------------------

    /**
     * Returns the total size (in cells) of this ship.
     *
     * @return number of cells occupied by the ship
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the number of hits registered on this ship.
     *
     * @return current hit count
     */
    public int getHits() {
        return hits;
    }

    /**
     * Increments the hit counter by one.
     * <p>Call this when the ship is successfully hit during the game.</p>
     */
    public void registerHit() {
        hits++;
    }

    /**
     * Returns the ship display name.
     *
     * @return ship name
     */
    public String getName() {
        return name;
    }

    /**
     * Convenience method to set all placement attributes at once.
     *
     * @param row        zero-based starting row
     * @param col        zero-based starting column
     * @param direction  direction label for placement
     */
    public void setPlacement(int row, int col, String direction) {
        this.row = row;
        this.col = col;
        this.direction = direction;
    }

    /**
     * @return zero-based starting row of the ship
     */
    public int getRow() {
        return row;
    }

    /**
     * @return zero-based starting column of the ship
     */
    public int getCol() {
        return col;
    }

    /**
     * @return placement direction label
     */
    public String getDirection() {
        return direction;
    }

    /**
     * Returns a string representation for debugging purposes.
     *
     * @return string with the ship's core attributes
     */
    @Override
    public String toString() {
        return "Ship{" +
                "size=" + size +
                ", name='" + name + '\'' +
                ", row=" + row +
                ", col=" + col +
                ", direction='" + direction + '\'' +
                '}';
    }
}
