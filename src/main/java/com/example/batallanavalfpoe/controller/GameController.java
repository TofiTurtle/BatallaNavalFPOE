package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.GameBoard;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

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
    private OpponentStage opponentStage;

    @FXML
    private ImageView img;

    private GameBoard playerBoard = new GameBoard(10, 10);
    /*creamos un opponentBoard, mas abajo copiamos sus datos con el opcontroller*/
    private GameBoard opponentBoard = new GameBoard(10, 10);


    private String shipDirection = "RIGHT"; // Dirección por defecto

    private Rectangle selectedShip = null;
    private int selectedShipSize = 0;
    private Map<Rectangle, Integer> shipSizeMap = new HashMap<>();
    private Map<Rectangle, ImagePattern> shipImageMap = new HashMap<>();

    private Image pendingCharacterImage;

    /*se crea una variable boolean que reresentara los turnos de disparo, siendo el
    * true para indicar disparo de usuario, y el false para disparo de maquina.*/
    private boolean shootingTurn = true; //true == le toca al player :V:V:V
    private boolean gridDisabled = true; //literal es solo un indicador extra para manejar los disparos



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
        opponentStage = new OpponentStage();

        playerGrid = new GridPane();
        playerGridContainer.getChildren().add(playerGrid);
        playerBoard.setupGrid(playerGrid); //esta vaina crea el gridpain de la izq

        opponentGrid = new GridPane();
        mainGridContainer.getChildren().add(opponentGrid);
        opponentBoard.setupGrid(opponentGrid); //esta vaina crea el gridpain de la derec

        deactivateGrid(opponentGrid);
        opponentButton.setDisable(true);

        copyOpponentShips();

        if (pendingCharacterImage != null) {
            img.setImage(pendingCharacterImage);
        }

        // Crear celdas para playerGrid con evento click
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row;
                final int c = col;
                Rectangle cell = playerBoard.createCell(r, c);
                cell.setOnMouseClicked(e -> handlePlayerGridClick(e, r, c));
                playerGrid.add(cell, c, r);
            }
        }

        // Inicializar flota y mapas
        for (Node child : fleetVBox.getChildren()) {
            if (child instanceof Rectangle rect) {
                int size = (int) (rect.getWidth() / 40);
                shipSizeMap.put(rect, size);

                ImagePattern pattern = switch (size) {
                    case 1 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/frigate_right.png"))));
                    case 2 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/destroyer_right.png"))));
                    case 3 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/submarine_right.png"))));
                    case 4 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/carrier_right.png"))));
                    default -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/default_right.png"))));
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

        playButton.setDisable(true);
    }

    private void selectShip(Rectangle ship) {
        if (selectedShip != null) {
            selectedShip.setStroke(Color.BLACK);
            selectedShip.setStrokeWidth(1);
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

    private void handleMachineGridClick(MouseEvent event, int row, int col) {
        if(gridDisabled) return; //si disabled, faltan barcos, no haga nada

        if(shootingTurn != true ) return; //si no tiene el turno, salga (aunque esto nunca ejecuta tecnicamente)

        //inicializamos variables
        int shottRow = row;
        int shotCol = col;

        //Valida ahi breve que el tiro que se quiere hacer SI este en la grilla, sino se cancela
        if (!opponentBoard.isWithinBounds(shottRow, shotCol))
            return;

        //aqui haria el playerShot(row,col) pa guardar el tiro
        /*ignorar esto, esto lo dejo aca pq seria necesario para serializar mas tarde*/

        //creamos la figura del rectangulo para simular graficamente el tiro
        double width = 40;
        double height = 40;
        Rectangle shotRectangle = new Rectangle(width, height);
        shotRectangle.setStroke(Color.GREEN); //color vistoso pa confirmar q sise pone

        //lo mostramos en el opponent grid
        opponentGrid.add(shotRectangle, shotCol, shottRow);

        //ahora hagamos la respectiva comprobacion de hit o miss
        if (opponentBoard.isOccupied(shottRow, shotCol)) {
            System.out.println("SHOT!!!! siuuu++++++++++++++++++");
            shootingTurn = true; //sigue teniendo el turno, puede acceder al evento again

        } else {
            System.out.println("MISS!!!! awwww------------------");
            shootingTurn = false; //pierde el turno
            processMachineShot(); //llama a la maquina para que tire
        }

    }
    @FXML
    private ImageView doctorimg;

    private void processMachineShot() {
        doctorimg.setVisible(true);
        /*en escensia pausetransicion es una clase diseñada literal para "congelar" procesos
        * del programa, NO los congela, da una ilusion*/
        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        /*Setonfinish.pause dice QUE COSAS VA A EJECUTAR despues del tiempo de pausa (1500ms)
        * lo q este dentro de esos { es lo q se va a retrasar el x tiempo*/
        pause.setOnFinished(event -> {
            boolean machineHit = true;
            do {

                //creamos el tiro de la maquina de manera aleatoria
                Random random = new Random();
                int MachineshotRow = random.nextInt(10);
                int MachineshotCol = random.nextInt(10);

                //aca creamos el respectivo rectanuglo para simular el tiro de maquina
                double width = 40;
                double height = 40;
                Rectangle machineShotRectangle = new Rectangle(width, height);
                machineShotRectangle.setStroke(Color.RED); //color vistoso pa confirmar q sise pone

                //colocamos en nuestro playergrid donde cayo el tiro, para corroborar q si se hizo
                playerGrid.add(machineShotRectangle, MachineshotCol, MachineshotRow);

                //condicional para comprobar x2 si el comportamiento es adecuado + salir del dowhile
                if(playerBoard.isOccupied(MachineshotRow, MachineshotCol)) {
                    System.out.println("NOS DIEROOOON");
                    machineHit = true;
                }else {
                    System.out.println("La maquina FALLO");
                    machineHit = false;
                }

            }while (machineHit);
            //si sale del dowhhile es que fallo, entonces si fallo se le devuelve el turno a player
            shootingTurn = true;
            doctorimg.setVisible(false);

        });
        pause.play(); //esto se pone como "activador" del evento, si esta vaina, NO se ejecuta ni mrd

    }


    private void handlePlayerGridClick(MouseEvent event, int row, int col) {
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

        //OJO atento creo q Este condicional sobra, pues en la funcion de abajo ya se hace la verificacion
      // if (!playerBoard.isWithinBounds(startRow, startCol)) {
        //    return; // Fuera de límites
       //}

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

        // para rotar la imagen junto con el recangulo se recibe el tamaño del barco y se le asigna un nombre dependiendo
        String imageName = switch (selectedShipSize) {
            case 1 -> "frigate";
            case 2 -> "destroyer";
            case 3 -> "submarine";
            case 4 -> "carrier";
            default -> "default";
        };

        /*aca recibe la direccion que se desea rotar, y dependiendo del tamaño del barco (imagename)
        se le asigna la imagen en esa direccion*/
        
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

    private void copyOpponentShips() {

        // 2. Obtenemos los barcos guardados del OpponentController
        List<OpponentController.PlacedShip> placedShips = OpponentController.getSavedPlacedShips();
        if (placedShips == null) return; // Si no hay barcos, salimos

        //***************************************************************************
        /*OJO VIVISIMO: este fragmento de codigo es sumamente importante, pues aqui, en vista de
        * que se tienen dos instancias del Oboard, pues con copiamos los datos para
        * trabajar bajo las mismas vueltas*/
        //Osea, esto es importante, estamos copiando los datos para usar en OponentBoard
        for (OpponentController.PlacedShip ps : placedShips) {
            opponentBoard.placeShip(
                    ps.placement.row,
                    ps.placement.col,
                    ps.ship.getSize(),
                    ps.placement.direction
            );
        }
        //*************************************************************************

        // 3. Tamaño de cada celda
        double cellSize = 40;

        // 4. Recorremos cada barco colocado
        for (OpponentController.PlacedShip ps : placedShips) {
            double width = cellSize;
            double height = cellSize;

            // 5. Determinamos orientación (vertical u horizontal)
            boolean vertical = ps.placement.direction.equals("UP") || ps.placement.direction.equals("DOWN");

            if (vertical) {
                height = ps.ship.getSize() * cellSize; // Altura ajustada para barcos verticales
            } else {
                width = ps.ship.getSize() * cellSize;  // Ancho ajustado para barcos horizontales
            }

            // 6. Creamos un rectángulo que representa el barco
            Rectangle rect = new Rectangle(width, height);
            rect.setFill(Color.TRANSPARENT); //usamos este color para que trin
            rect.setStroke(Color.TRANSPARENT);

            // 9. Ajustamos orientación para LEFT y DOWN (espejo)
            switch (ps.placement.direction) {
                case "LEFT" -> rect.setScaleX(-1);
                case "DOWN" -> rect.setScaleY(-1);
            }

            // 10. Agregamos el rectángulo en el GridPane (en la posición correcta)
            opponentGrid.add(rect, ps.placement.col, ps.placement.row);

            // 11. Ajustamos el tamaño del barco en filas o columnas según orientación
            if (vertical) {
                GridPane.setRowSpan(rect, ps.ship.getSize());
            } else {
                GridPane.setColumnSpan(rect, ps.ship.getSize());
            }
        }
    }


    @FXML
    private void handlePlayButton() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row;
                final int c = col;
                Rectangle cell = opponentBoard.createCell(row, col);
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
        gridDisabled = false; //asdasdasdasdadasd
        //asdasd

    }

    @FXML
    private void goToWelcomeStage(ActionEvent event) throws IOException {
        new WelcomeStage().show();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void showOpponentBoard(ActionEvent event) throws IOException {
        opponentStage.show();
    }
}
