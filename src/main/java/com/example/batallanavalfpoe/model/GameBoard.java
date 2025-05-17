package com.example.batallanavalfpoe.model;

public class GameBoard {
    private boolean[][] board;

    public GameBoard(int rows, int cols) {
        this.board = new boolean[rows][cols];
    }

    public boolean isOccupied(int row, int col) {
        return board[row][col];
    }

    public void setOccupied(int row, int col) {
        board[row][col] = true;
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < board.length && col >= 0 && col < board[0].length;
    }

}
