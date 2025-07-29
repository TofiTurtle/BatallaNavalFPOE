package com.example.batallanavalfpoe.model;

/**
 * Represents a player profile in the game, including display name
 * and the path to the selected character image.
 *
 * <p>The class acts as a simple data holder used by UI and game logic
 * to show player information during the match.</p>
 *
 * @since 1.0
 */
public class Player {

    /** Display name chosen by the player. */
    private String playerName;

    /** Filesystem or classpath location of the player's character image. */
    private String characterImagePath;

    /**
     * Creates a new {@code Player} with the provided name and character image path.
     *
     * @param playerName          visible name of the player
     * @param characterImagePath  path or resource identifier for the character image
     */
    public Player(String playerName, String characterImagePath) {
        this.playerName = playerName;
        this.characterImagePath = characterImagePath;
    }

    /**
     * @return the player's display name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return the path or resource identifier of the player's character image
     */
    public String getCharacterImagePath() {
        return characterImagePath;
    }

    /**
     * Updates the player's display name.
     *
     * @param playerName new display name
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Updates the path or resource identifier for the player's character image.
     *
     * @param characterImagePath new image path or resource identifier
     */
    public void setCharacterImagePath(String characterImagePath) {
        this.characterImagePath = characterImagePath;
    }
}
