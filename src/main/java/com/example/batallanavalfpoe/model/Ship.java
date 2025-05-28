package com.example.batallanavalfpoe.model;

import java.io.Serializable;

public class Ship implements Serializable {
    private int size;
    private String name;
    private int row;
    private int col;
    private String direction;
    //nuevo atributo de hits idea smaul
    private int hits;

    public Ship(int size, String name, int hits, String direction) {
        this.size = size;
        this.name = name;
        this.hits = hits; //inicializamos hits en 0 siemrep
        this.direction = direction; //ojo, ahora la inicializacion de ships necesitamos tener su direccion

    }
    //nuevos metodos añadidos que sirven para settear info del ship (se usa en PlaceShip de Gboard)
    public void setRow(int row) {
        this.row = row;
    }
    public void setCol(int col) {
        this.col = col;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }


    // Getters y Setters
    public int getSize() { //totalmente necesario para realizar posterior comparacion con los hits
        return size;
    }
    //respectivo metodo ara obtener los hist
    public int getHits() { //getter de hits
        return hits;
    }
    public void registerHit(){ //para sumar hits cuando lo golpeen, es un setter
        hits++;
    }

    public String getName() {
        return name;
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
        return "Ship{" +
                "size=" + size +
                ", name='" + name + '\'' +
                ", row=" + row +
                ", col=" + col +
                ", direction='" + direction + '\'' +
                '}';
    }
}