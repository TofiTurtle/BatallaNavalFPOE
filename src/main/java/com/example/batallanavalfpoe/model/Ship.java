package com.example.batallanavalfpoe.model;

public class Ship {
    private int size;
    private String name;
    private int row;
    private int col;
    private String direction;

    public Ship(int size, String name) {
        this.size = size;
        this.name = name;
    }

    // Getters y Setters
    public int getSize() {
        return size;
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