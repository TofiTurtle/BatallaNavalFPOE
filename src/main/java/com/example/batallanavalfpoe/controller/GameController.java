package com.example.batallanavalfpoe.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class GameController {
    @FXML
    private ImageView img;

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
    //AQUI CREO UNA LISTA DE BOOL
    private boolean[][] playerBoard = new boolean[10][10]; // Simulador de tablero

    //NECESITO QUE INTENTENTEN ARREGLAR COMO SE MUEVE EL BARCO DEBIDO A QUE CON BOOLEANOS NO FUNCIONA BIEN
    private boolean isVertical = false; // false = horizontal, true = vertical

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

    //ESTA FUNCION LA CREE PARA QUE SE PUEDA CAMBIAR EL NOMBRE QUE SE INGRESA TAL COMO EN LA IMAGEN(FUNCION DE ARRIBA)
    public void setNameLabel(String text) {
        nameLabel.setText(text);
    }
    @FXML
    private void initialize(){
        playerGrid = new GridPane();
        playerGridContainer.getChildren().add(playerGrid); // creamos el gridpane dinamico
        setupGrid(playerGrid);

        opponentGrid = new GridPane();
        mainGridContainer.getChildren().add(opponentGrid);
        setupGrid(opponentGrid);

        // Desactivar el GridPane del oponente inicialmente
        deactivateGrid(opponentGrid);

        if (!fleetVBox.getChildren().isEmpty()){
            playButton.setDisable(true);
        } else {
            playButton.setDisable(false);
        }

        if (pendingCharacterImage != null) {
            img.setImage(pendingCharacterImage); // para asignar la imagen
        }

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = createCell(row, col);
                playerGrid.add(cell, col, row); // asignamos los índices de columna y fila al ridpne
            }
        }

        /* obtiene los hijos de fleetvbox y si son una instancia de rectangle (las flotas) lo guarda en una variable y
        obtiene el tamaño del rectangulo, luego guarda este tamaño en el map con la flota y u tamaño
        */

        if (fleetVBox != null) {
            for (int i = 0; i < fleetVBox.getChildren().size(); i++) {
                Node child = fleetVBox.getChildren().get(i);
                if (child instanceof Rectangle) {
                    Rectangle rect = (Rectangle) child;
                    int size = (int) (rect.getWidth() / 40); // cada celda mide 40px
                    shipSizeMap.put(rect, size);

                    // elegir imagen segu tamaño del barco
                    ImagePattern pattern = switch (size) {
                        case 1 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/fragata.png"))));
                        case 2 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/destructor.png"))));
                        case 3 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/submarino.png"))));
                        case 4 -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/portaviones.png"))));
                        default -> new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/default.png"))));
                    };

                    rect.setFill(pattern); // aplicar imagen al barco del vbox
                    shipImageMap.put(rect, pattern); // guardar fotico para usar luego

                    rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            selectShip(rect); // llama a selectship cuando se le da click
                        }
                    });
                }
            }

            /*
            ESTE EVENTO DE TECLADO LLAMA A LA FUNCION SET ROTATE DEPENDIENDO SI SE USAN LAS FLECHAS DE ARRIBA ABAJO O DERECHA A IZQUIERDA
            ADEMAS SE PONE EL BOOLEANO RESPECTIVAMENTE PERO ESTE BOOLENAO AFECTA PRACTICAMENTE A TODOS LOS BLOQUES DEPUES DE GIRAR
            ENTONCES SI PUEDEN AGREGAR UN FUNCIO EXTRA PARA ROTAR DEPENDIENDO DE EL ANGULO SE LOS AGRADECERIA
                         */
            playerGridContainer.setOnKeyPressed(event -> {
                if (selectedShip == null) return;

                KeyCode code = event.getCode();
                if ((code == KeyCode.UP || code == KeyCode.DOWN) && !isVertical) {
                    isVertical = true;
                    selectedShip.setRotate(90);      // gira el rectángulo 90º
                }
                else if ((code == KeyCode.LEFT || code == KeyCode.RIGHT) && isVertical) {
                    isVertical = false;
                    selectedShip.setRotate(0);       // vuelve a horizontal
                }
            });

            Platform.runLater(() -> {
                playerGridContainer.requestFocus();
            });
        }

        playerGridContainer.setFocusTraversable(true);
        playerGridContainer.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP, DOWN -> isVertical = true;
                case LEFT, RIGHT -> isVertical = false;
            }
        });

        Platform.runLater(() -> playerGridContainer.requestFocus());
    }

    private void selectShip(Rectangle ship) {
        // borrar selección previa
        if (selectedShip != null) {
            selectedShip.setStroke(null);
            selectedShip.setRotate(0);  // resetea rotación si cambiabas seleccion
        }

        selectedShip = ship;
        selectedShipSize = shipSizeMap.get(ship);
        ship.setStroke(Color.YELLOW);
        ship.setStrokeWidth(3);

        // pon al pane en foco justo al seleccionar, para que capture el keyEvent
        playerGridContainer.requestFocus();
    }

    private Rectangle createCell(int row, int col) {
        Rectangle cell = new Rectangle(40, 40); // cada celda es un cuadrado de 40x40 píxeles
        cell.setFill(Color.LIGHTGRAY); // color de fondo de la celda
        cell.setStroke(Color.BLACK); // Borde negro de la celda
        cell.setOnMouseClicked(e -> handleGridClick(e, row, col)); // Se hace clic sobre la celda
        return cell;
    }

    // Metodo para agregar las celdas al GridPane
    private void setupGrid(GridPane gridPane) {
        gridPane.setGridLinesVisible(true);
        gridPane.setPrefSize(400, 400);
        gridPane.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        for (int i = 0; i < 10; i++) {
            gridPane.getColumnConstraints().add(new ColumnConstraints(40));
            gridPane.getRowConstraints().add(new RowConstraints(40));
        }
    }

    // Metodo para desactivar el GridPane del oponente
    private void deactivateGrid(GridPane grid) {
        for (Node node : grid.getChildren()) {
            if (node instanceof Rectangle) {
                node.setDisable(true);  // Desactivar todas las celdas
            }
        }
    }

    // Metodo para activar el GridPane del oponente y permitir que se hagan clics
    @FXML
    private void activateOpponentGrid() {
        // agregar las celdas a la cuadricula del oponente
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = createCell(row, col);
                opponentGrid.add(cell, col, row); // asignamos los indices de columna y fila al GridPane
            }
        }

        // Habilitar las celdas del oponente
        for (Node node : opponentGrid.getChildren()) {
            if (node instanceof Rectangle) {
                node.setDisable(false);  // Habilitar las celdas del oponente para permitir clics
            }
        }

        // verificar si todas las flotas han sido colocadas, si ningun nodo es una instancia de rectangle
        boolean emptyFleetVBox = fleetVBox.getChildren().stream()
                .noneMatch(n -> n instanceof Rectangle);

        // si todas las flotas han sido colocadas, habilitar el boton de jugar
        playButton.setDisable(!emptyFleetVBox);
    }

    // Colocar un barco sin modificar el tamaño del GridPane
    private void handleGridClick(MouseEvent event, int row, int col) {
        if (selectedShip == null) return; // Verifica que haya un barco seleccionado

        // Verifica si el barco cabe en la dirección seleccionada
        if (isVertical) {
            if (row + selectedShipSize > 10) return;
            for (int i = 0; i < selectedShipSize; i++) {
                if (playerBoard[row + i][col]) return; // Verifica superposición
            }
        } else {
            if (col + selectedShipSize > 10) return;
            for (int i = 0; i < selectedShipSize; i++) {
                if (playerBoard[row][col + i]) return; // Verifica superposición
            }
        }
        /*
        ESTO FUE LO QUE AGRUEGUE, BASICAMENTE RECORRE EL BARCO DE MANERA HORIZONTAL O VERTICAL DEPENDIENDO DEL BOOL
        Y MIRA EL ARREGLO DE PLAYERBOARD A VER SI ESAS CASILLAS NO ESTAN OCUPADAS
         */
        for (int i = 0; i < selectedShipSize; i++) {
            if (isVertical) playerBoard[row + i][col] = true;
            else playerBoard[row][col + i] = true;
        }

        // Lógica original para agregar el barco visualmente
        double width = isVertical ? 40 : selectedShipSize * 40;
        double height = isVertical ? selectedShipSize * 40 : 40;

        Rectangle shipRectangle = new Rectangle(width, height);
        shipRectangle.setFill(shipImageMap.get(selectedShip));

        playerGrid.add(shipRectangle, col, row);

        if (isVertical) {
            GridPane.setRowSpan(shipRectangle, selectedShipSize);
        } else {
            GridPane.setColumnSpan(shipRectangle, selectedShipSize);
        }

        // Eliminar el barco del VBox de flotas
        fleetVBox.getChildren().remove(selectedShip);
        shipSizeMap.remove(selectedShip);
        selectedShip = null;
    }

    // obtener nodo de una celda especifica
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            // Verifica si las celdas tienen el índice correcto
            if (columnIndex != null && columnIndex == col && rowIndex != null && rowIndex == row) {
                return node;
            }
        }
        return null;
    }

    // Metodo para regresar al WelcomeStage
    @FXML
    private void goToWelcomeStage(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/batallanavalfpoe/welcome-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}