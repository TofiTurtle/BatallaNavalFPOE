package com.example.batallanavalfpoe.model;

public class Ship {
    private int size;
    private String name;

    public Ship(int size, String name) {
        this.size = size;
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Ship{" + "size=" + size + ", name='" + name + '\'' + '}';
    }
}
