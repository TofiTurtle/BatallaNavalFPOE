package com.example.batallanavalfpoe.model;

import java.io.Serializable;

public class GameState implements Serializable {

    //Informacion del jugadorcito
    private Ship[][] playerShips;
    private boolean[][] playerShots;

    //informacion de la maquina
    private Ship[][] machineShips;
    private boolean[][] machineShots;

    public GameState(Ship[][] playerShips, boolean[][] playerShots,
                     Ship[][] machineShips, boolean[][] machineShots) {
        this.playerShips = playerShips;
        this.playerShots = playerShots;
        this.machineShips = machineShips;
        this.machineShots = machineShots;
    }

    // Getters, etas vainas son necesarias para despues volver a sacar esas cosas xd
    public Ship[][] getPlayerShips() {
        return playerShips;
    }

    public boolean[][] getPlayerShots() {
        return playerShots;
    }

    public Ship[][] getMachineShips() {
        return machineShips;
    }

    public boolean[][] getMachineShots() {
        return machineShots;
    }
}
