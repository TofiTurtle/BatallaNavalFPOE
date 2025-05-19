package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.GameBoard;
import com.example.batallanavalfpoe.view.OpponentStage;
import com.example.batallanavalfpoe.view.WelcomeStage;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameController {
    @FXML
    private StackPane playerGridContainer;

    @FXML
    private StackPane mainGridContainer;

    @FXML
    private VBox fleetVBox;

    @FXML
    private GridPane playerGrid;

    @FXML
    private GridPane opponentGrid;

    @FXML
    private Label nameLabel;

    @FXML
    private Button playButton;

    @FXML
    private HBox buttonsHBox;

    @FXML
    private Button opponentButton;

    @FXML
    private ImageView img;

    private GameBoard playerBoard = new GameBoard(10, 10);

    private String shipDirection = "RIGHT"; // Dirección por defecto

    private Rectangle selectedShip = null;
    private int selectedShipSize = 0;
    private Map<Rectangle, Integer> shipSizeMap = new HashMap<>();
    private Map<Rectangle, ImagePattern> shipImageMap = new HashMap<>();

    private Image pendingCharacterImage;

    public void setCharacterImage(Image image) {
        this.pendingCharacterImage = image;
        if (img != null) {
            img.setImage(image);
        }
    }

    public void setNameLabel(String text) {
        nameLabel.setText(text);
    }

    @FXML
    private void initialize() {
        playerGrid = new GridPane();
        playerGridContainer.getChildren().add(playerGrid);

        playerBoard.setupGrid(playerGrid);

        opponentGrid = new GridPane();
        mainGridContainer.getChildren().add(opponentGrid);

        playerBoard.setupGrid(opponentGrid);

        deactivateGrid(opponentGrid);
        opponentButton.setDisable(true);

        if (!fleetVBox.getChildren().isEmpty()){ // si el trin esta vacio entonces tran
            playButton.setDisable(true);
        } else {
            playButton.setDisable(false);
        }

        if (pendingCharacterImage != null) {
            img.setImage(pendingCharacterImage);
        }

        // Crear celdas para playerGrid con evento click
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row;
                final int c = col;
                Rectangle cell = playerBoard.createCell(r, c);
                cell.setOnMouseClicked(e -> handleGridClick(e, r, c));
                playerGrid.add(cell, c, r);
            }
        }

        // Inicializar flota y mapas
        for (Node child : fleetVBox.getChildren()) {
            if (child instanceof Rectangle rect) {
                int size = (int) (rect.getWidth() / 40);
                shipSizeMap.put(rect, size);

                ImagePattern pattern = switch (size) {
                    case 1 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/fragata.png"))));
                    case 2 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/destructor.png"))));
                    case 3 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/submarino.png"))));
                    case 4 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/portaviones.png"))));
                    default -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/default.png"))));
                };

                rect.setFill(pattern);
                shipImageMap.put(rect, pattern);

                rect.setOnMouseClicked(event -> selectShip(rect));
            }
        }

        // Evento teclado para cambiar dirección
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

        playButton.setDisable(!fleetVBox.getChildren().stream().anyMatch(n -> n instanceof Rectangle));
    }

    private void selectShip(Rectangle ship) {
        if (selectedShip != null) {
            selectedShip.setStroke(null);
            selectedShip.setStrokeWidth(0);
        }

        selectedShip = ship;
        selectedShipSize = shipSizeMap.get(ship);

        ship.setStroke(Color.YELLOW);
        ship.setStrokeWidth(3);

        shipDirection = "RIGHT";
        ship.setRotate(0);
        ship.setScaleX(1);
        ship.setScaleY(1);

        playerGridContainer.requestFocus();
    }

    private void handleGridClick(MouseEvent event, int row, int col) {
        if (selectedShip == null) return;

        int startRow = row;
        int startCol = col;

        // Ajustar posición inicial según dirección para que el barco quepa
        switch (shipDirection) {
            case "UP" -> startRow = row - (selectedShipSize - 1);
            case "LEFT" -> startCol = col - (selectedShipSize - 1);
            case "DOWN" -> startRow = row;
            case "RIGHT" -> startCol = col;
        }

        if (!playerBoard.isWithinBounds(startRow, startCol)) {
            return; // Fuera de límites
        }

        // Validar colocación usando el metodo del modelo
        if (!playerBoard.canPlaceShip(startRow, startCol, selectedShipSize, shipDirection)) {
            return; // No se puede colocar (ocupado o fuera de límites)
        }

        // Colocar barco en el modelo
        playerBoard.placeShip(startRow, startCol, selectedShipSize, shipDirection);

        // Crear rectángulo visual del barco
        double width = 40;
        double height = 40;
        if ("UP".equals(shipDirection) || "DOWN".equals(shipDirection)) {
            height = selectedShipSize * 40;
        } else {
            width = selectedShipSize * 40;
        }

        Rectangle shipRectangle = new Rectangle(width, height);
        shipRectangle.setFill(shipImageMap.get(selectedShip));

        switch (shipDirection) {
            case "LEFT" -> shipRectangle.setScaleX(-1);
            case "DOWN" -> shipRectangle.setScaleY(-1);
        }

        playerGrid.add(shipRectangle, startCol, startRow);

        if ("UP".equals(shipDirection) || "DOWN".equals(shipDirection)) {
            GridPane.setRowSpan(shipRectangle, selectedShipSize);
        } else {
            GridPane.setColumnSpan(shipRectangle, selectedShipSize);
        }

        // Remover barco de la flota visual y mapas
        fleetVBox.getChildren().remove(selectedShip);
        shipSizeMap.remove(selectedShip);
        shipImageMap.remove(selectedShip);
        selectedShip = null;

        boolean emptyFleet = fleetVBox.getChildren().stream().noneMatch(n -> n instanceof Rectangle);
        playButton.setDisable(!emptyFleet);

        // Si ya no quedan barcos, quitar el VBox de flota y centrar grillas
        boolean onlyLabelLeft = fleetVBox.getChildren().stream().allMatch(node -> !(node instanceof Rectangle));
        if (onlyLabelLeft) {
            Node stackPane = fleetVBox.getParent();
            if (stackPane != null && stackPane.getParent() instanceof HBox gridsHBox) {
                gridsHBox.getChildren().remove(stackPane);
                gridsHBox.setAlignment(Pos.CENTER);
            }
        }
    }

    private void deactivateGrid(GridPane grid) {
        playerBoard.deactivateGrid(grid);
    }

    @FXML
    private void handlePlayButton() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = playerBoard.createCell(row, col);
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
    }

    @FXML
    private void goToWelcomeStage(ActionEvent event) throws IOException {
        new WelcomeStage().show();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void showOpponentBoard(ActionEvent event) throws IOException {
        new OpponentStage().show();
    }
}
