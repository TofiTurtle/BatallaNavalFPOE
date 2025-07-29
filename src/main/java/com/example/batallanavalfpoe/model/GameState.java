package com.example.batallanavalfpoe.model;

import java.io.Serializable;

/**
 * Immutable snapshot of the match state used for saving and restoring a game.
 * <p>
 * This class stores both player and machine board information:
 * ships placement, shots taken, occupied cells, the UI title text,
 * and a compact summary of accumulated shots.
 * </p>
 * <p>
 * <strong>Note:</strong> Arrays are stored by reference. If you need strong immutability,
 * provide deep copies when constructing the {@code GameState}.
 * </p>
 *
 * @since 1.0
 */
public class GameState implements Serializable {

    /** Player's ship placement matrix. */
    private Ship[][] playerShips;

    /** Player's shots matrix (true if the cell has been shot). */
    private boolean[][] playerShots;

    /** Player's occupied cells matrix (true if occupied by a ship). */
    private boolean[][] occupiedPlayerCells;

    /** Machine's ship placement matrix. */
    private Ship[][] machineShips;

    /** Machine's shots matrix (true if the cell has been shot). */
    private boolean[][] machineShots;

    /** Machine's occupied cells matrix (true if occupied by a ship). */
    private boolean[][] occupiedMachineCells;

    /** Title text to restore in the UI. */
    private String titleText;

    /**
     * Accumulated shots data.
     * <ul>
     *   <li>index 0: total player shots</li>
     *   <li>index 1: total machine shots</li>
     * </ul>
     */
    int[] shotsData;

    /**
     * Constructs a new {@code GameState} with the provided board matrices and metadata.
     *
     * @param playerShips           player's ship matrix
     * @param playerShots           player's shots matrix
     * @param occupiedPlayerCells   player's occupied cells matrix
     * @param machineShips          machine's ship matrix
     * @param machineShots          machine's shots matrix
     * @param occupiedMachineCells  machine's occupied cells matrix
     * @param titleText             title text to restore in the UI
     * @param shotsData             accumulated shots data; index 0 = player, index 1 = machine
     */
    public GameState(Ship[][] playerShips, boolean[][] playerShots, boolean[][] occupiedPlayerCells,
                     Ship[][] machineShips, boolean[][] machineShots, boolean[][] occupiedMachineCells,
                     String titleText, int[] shotsData) {
        this.playerShips = playerShips;
        this.playerShots = playerShots;
        this.occupiedPlayerCells = occupiedPlayerCells;
        this.machineShips = machineShips;
        this.machineShots = machineShots;
        this.occupiedMachineCells = occupiedMachineCells;
        this.titleText = titleText;
        this.shotsData = shotsData;
    }

    // ---------------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------------

    /**
     * @return the player's ship matrix
     */
    public Ship[][] getPlayerShips() {
        return playerShips;
    }

    /**
     * @return the player's shots matrix
     */
    public boolean[][] getPlayerShots() {
        return playerShots;
    }

    /**
     * @return the player's occupied cells matrix
     */
    public boolean[][] getOccupiedPlayerCells() {
        return occupiedPlayerCells;
    }

    /**
     * @return the machine's ship matrix
     */
    public Ship[][] getMachineShips() {
        return machineShips;
    }

    /**
     * @return the machine's shots matrix
     */
    public boolean[][] getMachineShots() {
        return machineShots;
    }

    /**
     * @return the machine's occupied cells matrix
     */
    public boolean[][] getOccupiedMachineCells() {
        return occupiedMachineCells;
    }

    /**
     * @return the title text to restore in the UI
     */
    public String getTitleText() {
        return titleText;
    }

    /**
     * Updates the title text to be restored in the UI.
     *
     * @param titleText new title text
     */
    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    /**
     * @return total player shots saved in this snapshot (shotsData[0])
     * @throws IllegalStateException if {@code shotsData} is {@code null} or too short
     */
    public int getPlayerShotsSaved() {
        if (shotsData == null || shotsData.length < 1) {
            throw new IllegalStateException("shotsData is not initialized or has insufficient length.");
        }
        return shotsData[0];
    }

    /**
     * @return total machine shots saved in this snapshot (shotsData[1])
     * @throws IllegalStateException if {@code shotsData} is {@code null} or too short
     */
    public int getMachineShotsSaved() {
        if (shotsData == null || shotsData.length < 2) {
            throw new IllegalStateException("shotsData is not initialized or has insufficient length.");
        }
        return shotsData[1];
    }
}
