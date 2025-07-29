package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.GameBoard;
import com.example.batallanavalfpoe.model.GameState;
import com.example.batallanavalfpoe.model.SerializableFileHandler;
import com.example.batallanavalfpoe.model.Ship;
import com.example.batallanavalfpoe.view.OpponentStage;
import com.example.batallanavalfpoe.view.WelcomeStage;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

/**
 * Controller class for the main game screen in the naval battle game.
 * Handles ship placement, turn management, game state, and victory conditions.
 */
public class GameController {
    Font baseFont = Font.loadFont(getClass().getResourceAsStream("/com/example/batallanavalfpoe/fonts/Strjmono.ttf"), 25);
    @FXML private StackPane playerGridContainer;
    @FXML private StackPane mainGridContainer;
    @FXML private VBox fleetVBox;
    @FXML private GridPane playerGrid;
    @FXML private GridPane opponentGrid;
    @FXML private Label nameLabel;
    @FXML private Button playButton;
    @FXML private HBox buttonsHBox;
    @FXML private Button opponentButton;
    @FXML private ImageView img;
    @FXML private Label titleLabel;
    @FXML private Label fleetLabel;

    private GameBoard playerBoard = new GameBoard(10, 10);
    private OpponentStage opponentStage;

    /** Game board representing the opponent's ship placements */
    private GameBoard opponentBoard = new GameBoard(10, 10);

    /** Current ship placement direction (default: RIGHT) */
    private String shipDirection = "RIGHT";
    /** Currently selected ship for placement */
    private Rectangle selectedShip = null;
    /** Size of the currently selected ship */
    private int selectedShipSize = 0;
    /** Map storing ship sizes for visual elements */
    private Map<Rectangle, Integer> shipSizeMap = new HashMap<>();
    /** Map storing ship images for visual elements */
    private Map<Rectangle, ImagePattern> shipImageMap = new HashMap<>();
    /** Pending character image to display */
    private Image pendingCharacterImage;

    /**
     * Turn management flag:
     * true = player's turn to shoot
     * false = machine's turn to shoot
     */
    private boolean shootingTurn = true;
    /** Flag to disable grid interactions when not in play */
    private boolean gridDisabled = true;

    /** Handler for game state serialization */
    private SerializableFileHandler serializableFileHandler;

    /** Current game state for saved games */
    private GameState gameState;
    /** Count of successful player hits */
    private int playerHits = 0;
    /** Count of successful machine hits */
    private int machineHits = 0;

    /** Image patterns for hit/miss visualization */
    Image hit = new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/hit.png"));
    ImagePattern hitPattern = new ImagePattern(hit);
    Image space = new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/space.png"));
    ImagePattern spacePattern = new ImagePattern(space);
    Image sunk = new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/sunk.jpg"));
    ImagePattern sunkPattern = new ImagePattern(sunk);

    /**
     * Custom exception for invalid ship placement attempts
     */
    public class InvalidShipPlacementException extends Exception {
        public InvalidShipPlacementException(String message) {
            super(message);
        }
    }

    /**
     * Sets the current game state (for loading saved games)
     * @param gameState The game state to load
     */
    public void getGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Sets the character image for display
     * @param image The image to display
     */
    public void setCharacterImage(Image image) {
        this.pendingCharacterImage = image;
        try {
            if (img != null) {
                img.setImage(image);
            } else {
                throw new NullPointerException("ImageView 'img' is not initialized");
            }
        } catch (NullPointerException e) {
            System.err.println("Warning: " + e.getMessage());
        }
    }

    /**
     * Sets the player name label
     * @param text The name to display
     */
    public void setNameLabel(String text) {
        nameLabel.setText(text);
    }

    /**
     * Initializes the game controller
     * Sets up game boards and loads either new game or saved game state
     */
    @FXML
    private void initialize() {
        playerGrid = new GridPane();
        playerGridContainer.getChildren().add(playerGrid);
        mainGridContainer.setStyle("-fx-background-color: TRANSPARENT;");
        playerBoard.setupGrid(playerGrid);

        opponentGrid = new GridPane();
        mainGridContainer.getChildren().add(opponentGrid);
        mainGridContainer.setStyle("-fx-background-color: TRANSPARENT;");
        opponentBoard.setupGrid(opponentGrid);

        Platform.runLater(() -> {
            if (gameState == null) {
                System.out.println("ESTA JUGANDO DESDE 0-----------------");
                setupNewGame();
            } else {
                System.out.println("ESTA JUGANDO UNA PARTIDA YA INICIADA++++++++++++");
                loadSavedGame();
            }
        });

        serializableFileHandler = new SerializableFileHandler();
    }

    /**
     * Checks victory conditions and updates UI accordingly
     */
    private void winFunction() {
        if (playerHits == 20) {
            titleLabel.setText("Has conseguido la victoria");
            playerGrid.setDisable(true);
            opponentGrid.setDisable(true);
            playButton.setDisable(true);
            playButton.setVisible(false);
            opponentButton.setDisable(true);
            opponentButton.setVisible(false);
            buttonsHBox.getChildren().remove(playButton);
            buttonsHBox.getChildren().remove(opponentButton);
            buttonsHBox.setAlignment(Pos.CENTER);
        } else if (machineHits == 20) {
            titleLabel.setText("Has sido derrotado...");
            playerGrid.setDisable(true);
            opponentGrid.setDisable(true);
            playButton.setDisable(true);
            playButton.setVisible(false);
            opponentButton.setDisable(true);
            opponentButton.setVisible(false);
            buttonsHBox.getChildren().remove(playButton);
            buttonsHBox.getChildren().remove(opponentButton);
            buttonsHBox.setAlignment(Pos.CENTER);
        }
    }

    /**
     * Loads a saved game state and restores UI elements
     */
    private void loadSavedGame() {
        playButton.setText("Continuar");

        System.out.println(">> Cargando partida guardada...");

        titleLabel.setText(gameState.getTitleText());

        playerBoard.restoreBoard(
                gameState.getPlayerShips(),
                gameState.getPlayerShots(),
                gameState.getOccupiedPlayerCells()
        );
        opponentBoard.restoreBoard(
                gameState.getMachineShips(),
                gameState.getMachineShots(),
                gameState.getOccupiedMachineCells()
        );

        playerHits = gameState.getPlayerShotsSaved();
        System.out.println(playerHits);
        machineHits = gameState.getMachineShotsSaved();
        System.out.println(machineHits);
        winFunction();

        OpponentController.setRestoredFromSavedGame(true);

        opponentStage = new OpponentStage();
        opponentStage.getController().restoreFrom(
                gameState.getOccupiedMachineCells(),
                gameState.getMachineShips()
        );

        // Restore player grid
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row;
                final int c = col;

                Rectangle cell = playerBoard.createCell();
                cell.setOnMouseClicked(e -> handlePlayerGridClick(e, r, c));
                cell.setStyle("-fx-background-color: TRANSPARENT;");
                playerGrid.add(cell, c, r);
            }
        }

        // Restore player ships
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (playerBoard.getShips()[row][col] == null) continue;

                if (playerBoard.getShips()[row][col].getRow() == row &&
                        playerBoard.getShips()[row][col].getCol() == col) {

                    double width = 40;
                    double height = 40;
                    boolean vertical = playerBoard.getShips()[row][col].getDirection().equals("UP") ||
                            playerBoard.getShips()[row][col].getDirection().equals("DOWN");

                    if (vertical) {
                        height = playerBoard.getShips()[row][col].getSize() * 40;
                    } else {
                        width = playerBoard.getShips()[row][col].getSize() * 40;
                    }
                    Rectangle rect = new Rectangle(width, height);
                    String imageName = switch (playerBoard.getShips()[row][col].getSize()) {
                        case 1 -> "frigate";
                        case 2 -> "destroyer";
                        case 3 -> "submarine";
                        case 4 -> "carrier";
                        default -> "default";
                    };

                    String path = switch (playerBoard.getShips()[row][col].getDirection()) {
                        case "UP" -> "/com/example/batallanavalfpoe/images/" + imageName + "_up.png";
                        case "DOWN" -> "/com/example/batallanavalfpoe/images/" + imageName + "_down.png";
                        case "LEFT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_left.png";
                        case "RIGHT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_right.png";
                        default -> "/com/example/batallanavalfpoe/images/default_right.png";
                    };

                    try {
                        Image image = new Image(getClass().getResourceAsStream(path));
                        ImagePattern pattern = new ImagePattern(image);
                        rect.setFill(pattern);
                    } catch (Exception e) {
                        rect.setFill(Color.GRAY);
                    }

                    playerGrid.add(rect, playerBoard.getShips()[row][col].getCol(),
                            playerBoard.getShips()[row][col].getRow());

                    if (vertical) {
                        GridPane.setRowSpan(rect, playerBoard.getShips()[row][col].getSize());
                    } else {
                        GridPane.setColumnSpan(rect, playerBoard.getShips()[row][col].getSize());
                    }
                    rect.toFront();

                    // Add hit/miss effects
                    for (int rows = 0; rows < 10; rows++) {
                        for (int cols = 0; cols < 10; cols++) {
                            if (playerBoard.getshotsOnterritory(rows, cols)) {
                                Rectangle effect = new Rectangle(40, 40);
                                if (playerBoard.isOccupied(rows, cols)) {
                                    Ship hitShip = playerBoard.getShip(rows, cols);
                                    if (hitShip.getHits() >= hitShip.getSize()) {
                                        effect.setFill(sunkPattern);
                                    } else {
                                        effect.setFill(hitPattern);
                                    }
                                } else {
                                    effect.setFill(spacePattern);
                                }
                                playerGrid.add(effect, cols, rows);
                                effect.toFront();
                            }
                        }
                    }
                }
            }
        }

        // Restore opponent grid
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = opponentBoard.createCell();
                opponentGrid.add(cell, col, row);

                if (opponentBoard.getshotsOnterritory(row,col)) {
                    if (opponentBoard.isOccupied(row,col)) {
                        Ship hitShip = opponentBoard.getShip(row, col);
                        if (hitShip.getHits() >= hitShip.getSize()) {
                            cell.setFill(sunkPattern);
                        } else {
                            cell.setFill(hitPattern);
                        }
                    } else {
                        cell.setFill(spacePattern);
                    }
                }
            }
        }

        // Restore ship selection controls
        playerGridContainer.setOnKeyPressed(event -> {
            if (selectedShip == null) return;
            switch (event.getCode()) {
                case UP -> shipDirection = "UP";
                case DOWN -> shipDirection = "DOWN";
                case LEFT -> shipDirection = "LEFT";
                case RIGHT -> shipDirection = "RIGHT";
            }
        });

        Platform.runLater(() -> {
            playerGridContainer.requestFocus();
            playerGridContainer.setFocusTraversable(true);
        });

        // Restore fleet display
        List<Ship> fullFleet = generateFullFleet();
        List<Ship> placedShips = getPlacedShips(playerBoard.getShips());
        List<Ship> remainingShips = calculateRemainingShips(fullFleet, placedShips);
        fillFleetBox(fleetVBox, remainingShips);

        if(remainingShips.isEmpty()) {
            playButton.setDisable(false);
        } else {
            opponentButton.setDisable(true);
            playButton.setDisable(true);
        }

        buttonsHBox.setAlignment(Pos.CENTER);

        boolean onlyLabelLeft = fleetVBox.getChildren().stream().allMatch(node -> !(node instanceof Rectangle));
        if (onlyLabelLeft) {
            Node stackPane = fleetVBox.getParent();
            if (stackPane != null && stackPane.getParent() instanceof HBox gridsHBox) {
                gridsHBox.getChildren().remove(stackPane);
                gridsHBox.setAlignment(Pos.CENTER);
            }
        }

        System.out.println(">> Partida restaurada visualmente.");
    }

    /**
     * Generates the complete fleet configuration
     * @return List of all ships in the game
     */
    private List<Ship> generateFullFleet() {
        List<Ship> fleet = new ArrayList<>();

        for (int i = 0; i < 1; i++) fleet.add(new Ship(4, "Portaviones", 0,"default"));
        for (int i = 0; i < 2; i++) fleet.add(new Ship(3, "Submarino", 0,"default"));
        for (int i = 0; i < 3; i++) fleet.add(new Ship(2, "Destructor", 0,"default"));
        for (int i = 0; i < 4; i++) fleet.add(new Ship(1, "Fragata", 0,"default"));
        return fleet;
    }

    /**
     * Gets all ships currently placed on the board
     * @param shipMatrix The ship placement matrix
     * @return List of placed ships
     */
    private List<Ship> getPlacedShips(Ship[][] shipMatrix) {
        List<Ship> placed = new ArrayList<>();

        for (int row = 0; row < shipMatrix.length; row++) {
            for (int col = 0; col < shipMatrix[0].length; col++) {
                Ship s = shipMatrix[row][col];
                if (s != null && !placed.contains(s)) {
                    placed.add(s);
                }
            }
        }
        return placed;
    }

    /**
     * Calculates which ships still need to be placed
     * @param fullFleet Complete fleet configuration
     * @param placedS Ships already placed
     * @return List of remaining ships to place
     */
    private List<Ship> calculateRemainingShips(List<Ship> fullFleet, List<Ship> placedS) {
        List<Ship> remaining = new ArrayList<>(fullFleet);

        for (Ship placed : placedS) {
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i).getSize() == placed.getSize()) {
                    remaining.remove(i);
                    break;
                }
            }
        }
        return remaining;
    }

    /**
     * Populates the fleet display with remaining ships
     * @param fleetVBox Container for fleet display
     * @param remaining Ships still needing placement
     */
    private void fillFleetBox(VBox fleetVBox, List<Ship> remaining) {
        fleetVBox.getChildren().clear();
        fleetLabel.setVisible(true);
        fleetVBox.getChildren().add(fleetLabel);

        for (Ship ship : remaining) {
            int size = ship.getSize();
            Rectangle rect = new Rectangle(size * 40, 40);

            ImagePattern pattern = switch (size) {
                case 1 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/frigate_right.png")));
                case 2 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/destroyer_right.png")));
                case 3 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/submarine_right.png")));
                case 4 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/carrier_right.png")));
                default -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/default_right.png")));
            };

            rect.setFill(pattern);
            shipSizeMap.put(rect, size);
            shipImageMap.put(rect, pattern);
            rect.setOnMouseClicked(event -> selectShip(rect));
            fleetVBox.getChildren().add(rect);
        }
    }

    /**
     * Sets up a new game with initial configurations
     */
    private void setupNewGame() {
        saveGame();
        OpponentController.setRestoredFromSavedGame(false);
        opponentStage = new OpponentStage();

        deactivateGrid(opponentGrid);
        opponentButton.setDisable(true);
        copyOpponentShips();

        if (pendingCharacterImage != null) {
            img.setImage(pendingCharacterImage);
        }

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row;
                final int c = col;
                Rectangle cell = playerBoard.createCell();
                cell.setOnMouseClicked(e -> handlePlayerGridClick(e, r, c));
                cell.setStyle("-fx-background-color: TRANSPARENT;");
                playerGrid.add(cell, c, r);
            }
        }

        for (Node child : fleetVBox.getChildren()) {
            if (child instanceof Rectangle rect) {
                int size = (int) (rect.getWidth() / 40);
                shipSizeMap.put(rect, size);

                ImagePattern pattern = switch (size) {
                    case 1 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/frigate_right.png")));
                    case 2 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/destroyer_right.png")));
                    case 3 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/submarine_right.png")));
                    case 4 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/carrier_right.png")));
                    default -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/default_right.png")));
                };

                rect.setFill(pattern);
                shipImageMap.put(rect, pattern);
                rect.setOnMouseClicked(event -> selectShip(rect));
            }
        }

        playerGridContainer.setOnKeyPressed(event -> {
            if (selectedShip == null) return;
            switch (event.getCode()) {
                case UP -> shipDirection = "UP";
                case DOWN -> shipDirection = "DOWN";
                case LEFT -> shipDirection = "LEFT";
                case RIGHT -> shipDirection = "RIGHT";
            }
        });

        Platform.runLater(() -> {
            playerGridContainer.requestFocus();
            playerGridContainer.setFocusTraversable(true);
        });

        playButton.setDisable(true);
    }

    /**
     * Selects a ship for placement
     * @param ship The ship rectangle to select
     */
    private void selectShip(Rectangle ship) {
        if (selectedShip != null) {
            selectedShip.setStroke(Color.BLACK);
            selectedShip.setStrokeWidth(1);
        }

        selectedShip = ship;
        selectedShipSize = shipSizeMap.get(ship);

        ship.setStroke(Color.WHITE);
        ship.setStrokeWidth(3);

        shipDirection = "RIGHT";
        ship.setRotate(0);
        ship.setScaleX(1);
        ship.setScaleY(1);

        playerGridContainer.requestFocus();
    }

    /**
     * Handles player shots on the opponent grid
     * @param event Mouse click event
     * @param row Grid row coordinate
     * @param col Grid column coordinate
     */
    private void handleMachineGridClick(MouseEvent event, int row, int col) {
        if(gridDisabled) return;
        if(!shootingTurn) return;

        int shotRow = row;
        int shotCol = col;

        if (!opponentBoard.isWithinBounds(shotRow, shotCol))
            return;

        double width = 40;
        double height = 40;
        Rectangle shotRectangle = new Rectangle(width, height);
        shotRectangle.toFront();
        opponentGrid.add(shotRectangle, shotCol, shotRow);
        opponentBoard.setShotsOnterritory(shotRow, shotCol);

        if (opponentBoard.isOccupied(shotRow, shotCol)) {
            playerHits++;
            System.out.println(playerHits);
            winFunction();

            Ship hitShip = opponentBoard.getShip(shotRow, shotCol);
            hitShip.registerHit();

            if (hitShip.getHits() >= hitShip.getSize()) {
                System.out.println("HUNDIDO!!! 🔥 El " + hitShip.getName() + " ha sido destruido por el JUGADOR.");
                shotRectangle.setFill(sunkPattern);

                int startRow = hitShip.getRow();
                int startCol = hitShip.getCol();
                int size = hitShip.getSize();
                String direction = hitShip.getDirection();
                System.out.println("startrow: "+ startRow + "\nstartcol" + startCol + "\nsize" + size + "\n direction" + direction);

                for (int i = 0; i < size; i++) {
                    int currentRow = startRow;
                    int currentCol = startCol;

                    switch (direction) {
                        case "UP" -> currentRow = startRow + i;
                        case "DOWN" -> currentRow = startRow + i;
                        case "LEFT" -> currentCol = startCol + i;
                        case "RIGHT" -> currentCol = startCol + i;
                    }
                    Rectangle rect = new Rectangle(40, 40);
                    rect.setFill(sunkPattern);
                    opponentGrid.add(rect, currentCol, currentRow);
                }
            } else {
                System.out.println("TOCADO!!! 💥 Al " + hitShip.getName() + " Haz acertado tu Tiro! intente de nevo");
                shotRectangle.setFill(hitPattern);
            }

            saveGame();
            shootingTurn = true;
        } else {
            System.out.println("MISS!!!! awwww------------------");
            shotRectangle.setFill(spacePattern);
            saveGame();
            shootingTurn = false;
            opponentGrid.setDisable(true);
            processMachineShot();
        }
    }

    /**
     * Processes the machine's shooting turn with a delay
     */
    private void processMachineShot() {
        shootingTurn = false;

        PauseTransition thinkingPause = new PauseTransition(Duration.millis(1500));
        thinkingPause.setOnFinished(e -> {
            Random random = new Random();
            int MachineshotRow = random.nextInt(10);
            int MachineshotCol = random.nextInt(10);

            double width = 40;
            double height = 40;
            Rectangle machineShotRectangle = new Rectangle(width, height);

            playerGrid.add(machineShotRectangle, MachineshotCol, MachineshotRow);
            playerBoard.setShotsOnterritory(MachineshotRow, MachineshotCol);

            if(playerBoard.isOccupied(MachineshotRow, MachineshotCol)) {
                machineHits++;
                System.out.println(machineHits);
                winFunction();

                Ship hitShip = playerBoard.getShip(MachineshotRow, MachineshotCol);
                hitShip.registerHit();

                if (hitShip.getHits() >= hitShip.getSize()) {
                    System.out.println("HUNDIDO!!! 🔥 El " + hitShip.getName() + " ha sido destruido por la MAQUINA.");
                    machineShotRectangle.setFill(sunkPattern);

                    int startRow = hitShip.getRow();
                    int startCol = hitShip.getCol();
                    int size = hitShip.getSize();
                    String direction = hitShip.getDirection();
                    System.out.println("startrow: "+ startRow + "\nstartcol" + startCol + "\nsize" + size + "\n direction" + direction);

                    for (int i = 0; i < size; i++) {
                        int currentRow = startRow;
                        int currentCol = startCol;

                        switch (direction) {
                            case "UP" -> currentRow = startRow + i;
                            case "DOWN" -> currentRow = startRow + i;
                            case "LEFT" -> currentCol = startCol + i;
                            case "RIGHT" -> currentCol = startCol + i;
                        }
                        Rectangle rect = new Rectangle(40, 40);
                        rect.setFill(sunkPattern);
                        playerGrid.add(rect, currentCol, currentRow);
                    }
                } else {
                    System.out.println("TOCADO!!! 💥 Al " + hitShip.getName() + " lo ha tocado la MAQUINA.");
                    machineShotRectangle.setFill(hitPattern);
                }

                saveGame();
                processMachineShot();
            } else {
                System.out.println("La maquina FALLO");
                machineShotRectangle.setFill(spacePattern);
                saveGame();
                opponentGrid.setDisable(false);
            }
        });
        thinkingPause.play();
        shootingTurn = true;
    }

    /**
     * Handles ship placement on player grid
     * @param event Mouse click event
     * @param row Grid row coordinate
     * @param col Grid column coordinate
     */
    private void handlePlayerGridClick(MouseEvent event, int row, int col) {
        if (selectedShip == null) return;

        int startRow = row;
        int startCol = col;

        switch (shipDirection) {
            case "UP" -> startRow = row - (selectedShipSize - 1);
            case "LEFT" -> startCol = col - (selectedShipSize - 1);
            case "DOWN" -> startRow = row;
            case "RIGHT" -> startCol = col;
        }

        try {
            if (!playerBoard.canPlaceShip(startRow, startCol, selectedShipSize, shipDirection)) {
                throw new InvalidShipPlacementException("No se puede colocar el barco aqui.");
            }
        } catch (InvalidShipPlacementException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }

        String shipName;
        switch (selectedShipSize) {
            case 1: shipName = "fragata";
                break;
            case 2: shipName = "destructor";
                break;
            case 3: shipName = "submarino";
                break;
            case 4: shipName = "portaaviones";
                break;
            default: shipName = "default";
                break;
        }

        Ship ship = new Ship(selectedShipSize, shipName, 0, shipDirection);
        playerBoard.placeShip(startRow, startCol, ship, shipDirection);

        double width = 40;
        double height = 40;
        if ("UP".equals(shipDirection) || "DOWN".equals(shipDirection)) {
            height = selectedShipSize * 40;
        } else {
            width = selectedShipSize * 40;
        }

        Rectangle shipRectangle = new Rectangle(width, height);

        String imageName = switch (selectedShipSize) {
            case 1 -> "frigate";
            case 2 -> "destroyer";
            case 3 -> "submarine";
            case 4 -> "carrier";
            default -> "default";
        };

        String path = switch (shipDirection) {
            case "UP" -> "/com/example/batallanavalfpoe/images/" + imageName + "_up.png";
            case "DOWN" -> "/com/example/batallanavalfpoe/images/" + imageName + "_down.png";
            case "LEFT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_left.png";
            case "RIGHT" -> "/com/example/batallanavalfpoe/images/" + imageName + "_right.png";
            default -> "/com/example/batallanavalfpoe/images/default_right.png";
        };

        Image directionImage = new Image(getClass().getResourceAsStream(path));
        ImagePattern pattern = new ImagePattern(directionImage);
        shipRectangle.setFill(pattern);

        playerGrid.add(shipRectangle, startCol, startRow);
        saveGame();

        if ("UP".equals(shipDirection) || "DOWN".equals(shipDirection)) {
            GridPane.setRowSpan(shipRectangle, selectedShipSize);
        } else {
            GridPane.setColumnSpan(shipRectangle, selectedShipSize);
        }

        fleetVBox.getChildren().remove(selectedShip);
        shipSizeMap.remove(selectedShip);
        shipImageMap.remove(selectedShip);
        selectedShip = null;

        boolean emptyFleet = fleetVBox.getChildren().stream().noneMatch(n -> n instanceof Rectangle);
        playButton.setDisable(!emptyFleet);

        boolean onlyLabelLeft = fleetVBox.getChildren().stream().allMatch(node -> !(node instanceof Rectangle));
        if (onlyLabelLeft) {
            Node stackPane = fleetVBox.getParent();
            if (stackPane != null && stackPane.getParent() instanceof HBox gridsHBox) {
                gridsHBox.getChildren().remove(stackPane);
                gridsHBox.setAlignment(Pos.CENTER);
            }
        }
    }

    /**
     * Deactivates grid interactions
     * @param grid The grid to deactivate
     */
    private void deactivateGrid(GridPane grid) {
        playerBoard.deactivateGrid(grid);
    }

    /**
     * Copies opponent ship placements from opponent stage
     */
    private void copyOpponentShips() {
        List<Ship> placedShips = OpponentController.getSavedPlacedShips();
        if (placedShips == null) return;

        for (Ship ship : placedShips) {
            opponentBoard.placeShip(
                    ship.getRow(),
                    ship.getCol(),
                    ship,
                    ship.getDirection()
            );
        }

        double cellSize = 40;
        for (Ship ship : placedShips) {
            double width = cellSize;
            double height = cellSize;
            boolean vertical = ship.getDirection().equals("UP") || ship.getDirection().equals("DOWN");

            if (vertical) {
                height = ship.getSize() * cellSize;
            } else {
                width = ship.getSize() * cellSize;
            }

            Rectangle rect = new Rectangle(width, height);
            rect.setFill(Color.TRANSPARENT);
            rect.setStroke(Color.TRANSPARENT);

            switch (ship.getDirection()) {
                case "LEFT" -> rect.setScaleX(-1);
                case "DOWN" -> rect.setScaleY(-1);
            }

            opponentGrid.add(rect, ship.getCol(), ship.getRow());

            if (vertical) {
                GridPane.setRowSpan(rect, ship.getSize());
            } else {
                GridPane.setColumnSpan(rect, ship.getSize());
            }
        }
    }

    /**
     * Handles play button click to start the game
     */
    @FXML
    private void handlePlayButton() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row;
                final int c = col;
                Rectangle cell = opponentBoard.createCell();
                cell.setStyle("-fx-background-color: TRANSPARENT;");
                cell.setOnMouseClicked(e -> handleMachineGridClick(e, r, c));
                opponentGrid.add(cell, col, row);
            }
        }

        for (Node node : opponentGrid.getChildren()) {
            if (node instanceof Rectangle) {
                node.setDisable(false);
            }
        }

        buttonsHBox.getChildren().remove(playButton);
        buttonsHBox.setAlignment(Pos.CENTER);
        opponentButton.setDisable(false);
        gridDisabled = false;

        titleLabel.setText("que la fuerza te acompañe...");
        fleetLabel.setVisible(false);
        winFunction();

        if (gameState != null) {
            gameState.setTitleText(titleLabel.getText());
        }

        saveGame();
    }

    /**
     * Returns to welcome stage
     * @param event Action event triggering the transition
     */
    @FXML
    private void goToWelcomeStage(ActionEvent event) {
        try {
            new WelcomeStage().show();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            opponentStage.close();
        } catch (IOException e) {
            System.err.println("Error al abrir la pantalla de bienvenida: " + e.getMessage());
        }
    }

    /**
     * Shows opponent board in separate stage
     * @param event Action event triggering the display
     * @throws IOException If stage cannot be shown
     */
    @FXML
    private void showOpponentBoard(ActionEvent event) throws IOException {
        opponentStage.show();
    }

    /**
     * Saves current game state to file
     */
    private void saveGame() {
        Ship[][] playerShips = playerBoard.getShips();
        boolean[][] playerShots = playerBoard.getShotsBoard();
        boolean[][] occupiedPlayerCells = playerBoard.getOccupiedCells();

        Ship[][] machineShips = opponentBoard.getShips();
        boolean[][] machineShots = opponentBoard.getShotsBoard();
        boolean[][] occupiedMachineCells = opponentBoard.getOccupiedCells();

        int playerShotSaved = playerHits;
        int machineShotSaved = machineHits;
        int[] shotsData = {playerShotSaved, machineShotSaved};

        GameState gameState = new GameState(
                playerShips, playerShots, occupiedPlayerCells,
                machineShips, machineShots, occupiedMachineCells,
                titleLabel.getText(), shotsData
        );

        serializableFileHandler.serialize("game_data.ser", gameState);

        System.out.println("Si se guardo manito, calma! :)))");
    }
}