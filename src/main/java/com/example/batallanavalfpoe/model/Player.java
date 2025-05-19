package com.example.batallanavalfpoe.model;

public class Player {
    private String playerName;
    private String characterImagePath;

    //constructor de la clase player xd
    public Player(String playerName, String characterImagePath) {
        this.playerName = playerName;
        this.characterImagePath = characterImagePath;

    }
    //Ahora necesitamos getter para poder obtener el nombre del mk y su imagen
    public String getPlayerName() {
        return playerName;
    }
    public String getCharacterImagePath() {
        return characterImagePath;
    }
    //Y por ultimo, unos setter pa quep cuando escriban en el texfil pues colocarlo
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setCharacterImagePath(String characterImagePath) { this.characterImagePath = characterImagePath; }
    //trin tran tripi
}
