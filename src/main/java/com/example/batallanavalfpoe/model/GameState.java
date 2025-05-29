package com.example.batallanavalfpoe.model;

import java.io.Serializable;

public class GameState implements Serializable {

    //Informacion del jugadorcito
    private Ship[][] playerShips;
    private boolean[][] playerShots;
    private boolean[][] occupiedPlayerCells;

    //informacion de la maquina
    private Ship[][] machineShips;
    private boolean[][] machineShots;
    private boolean[][] occupiedMachineCells;

    // pa restaurar el texto del titulo
    private String titleText;

    //para restaurar los tiros acumulados
    int[] shotsData;

    public GameState(Ship[][] playerShips, boolean[][] playerShots, boolean[][] occupiedPlayerCells,
                     Ship[][] machineShips, boolean[][] machineShots, boolean[][] occupiedMachineCells, String titleText, int[] shotsData) {
        this.playerShips = playerShips;
        this.playerShots = playerShots;
        this.occupiedPlayerCells = occupiedPlayerCells;
        this.machineShips = machineShips;
        this.machineShots = machineShots;
        this.occupiedMachineCells = occupiedMachineCells;
        this.titleText = titleText;
        this.shotsData = shotsData;
    }

    // Getters, etas vainas son necesarias para despues volver a sacar esas cosas xd
    public Ship[][] getPlayerShips() {
        return playerShips;
    }
    public boolean[][] getPlayerShots() {
        return playerShots;
    }
    public boolean[][] getOccupiedPlayerCells() {
        return occupiedPlayerCells;
    }

    public Ship[][] getMachineShips() {
        return machineShips;
    }
    public boolean[][] getMachineShots() {
        return machineShots;
    }
    public boolean[][] getOccupiedMachineCells() {
        return occupiedMachineCells;
    }

    public String getTitleText() {
        return titleText;
    }
    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }
    public int getPlayerShotsSaved(){
        return shotsData[0];
    }
    public int getMachineShotsSaved(){
        return shotsData[1];
    }
}
