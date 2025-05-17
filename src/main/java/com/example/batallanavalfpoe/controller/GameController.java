package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.GameBoard;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

    @FXML
    private HBox buttonsHBox;

    //AQUI CREO UNA LISTA DE BOOL
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

            playerGridContainer.setOnKeyPressed(event -> {
                if (selectedShip == null) return;

                switch (event.getCode()) {
                    case UP -> shipDirection = "UP";
                    case DOWN -> shipDirection = "DOWN";
                    case LEFT -> shipDirection = "LEFT";
                    case RIGHT -> shipDirection = "RIGHT";
                }
            });

            // Asegura que el contenedor reciba el foco de teclado
            Platform.runLater(() -> playerGridContainer.requestFocus());
            playerGridContainer.setFocusTraversable(true);
        }
    }

    private void selectShip(Rectangle ship) {
        // Restaurar estilo visual del barco anterior
        if (selectedShip != null) {
            selectedShip.setStroke(null);
            selectedShip.setStrokeWidth(0);
        }

        selectedShip = ship;
        selectedShipSize = shipSizeMap.get(ship);

        ship.setStroke(Color.YELLOW);
        ship.setStrokeWidth(3);

        // Reiniciar dirección por defecto y eliminar transformación visual
        shipDirection = "RIGHT";
        ship.setRotate(0); // sin rotación visual
        ship.setScaleX(1); // sin reflejo horizontal
        ship.setScaleY(1); // sin reflejo vertical

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
        

        //quitar boton jugar cuando se presiona el trin

        buttonsHBox.getChildren().remove(playButton);
        buttonsHBox.setAlignment(Pos.CENTER);
    }

    // Colocar un barco sin modificar el tamaño del GridPane
    private void handleGridClick(MouseEvent event, int row, int col) {
        if (selectedShip == null) return;

        // Variables para posición inicial real (la parte "más arriba" o "más izquierda" del barco)
        int startRow = row;
        int startCol = col;

        switch (shipDirection) {
            case "UP" -> startRow = row - (selectedShipSize - 1);
            case "LEFT" -> startCol = col - (selectedShipSize - 1);
            case "DOWN" -> startRow = row;
            case "RIGHT" -> startCol = col;
        }

        // Validar que la posición inicial esté dentro del tablero
        if (!playerBoard.isWithinBounds(startRow, startCol)) {
            return;
        }

        // Variables para el desplazamiento de acuerdo a la dirección
        int dRow = 0, dCol = 0;
        switch (shipDirection) {
            case "UP" -> dRow = 1;
            case "DOWN" -> dRow = 1;
            case "LEFT" -> dCol = 1;
            case "RIGHT" -> dCol = 1;
        }

        // Validar que las casillas estén dentro y libres
        for (int i = 0; i < selectedShipSize; i++) {
            int r = startRow + dRow * i;
            int c = startCol + dCol * i;

            if (!playerBoard.isWithinBounds(r, c) || playerBoard.isOccupied(r, c)) {
                return;
            }
        }

        // Marcar las casillas como ocupadas
        for (int i = 0; i < selectedShipSize; i++) {
            int r = startRow + dRow * i;
            int c = startCol + dCol * i;
            playerBoard.setOccupied(r, c);
        }

        // Tamaño del rectángulo a dibujar
        double width = 40, height = 40;
        if (shipDirection.equals("UP") || shipDirection.equals("DOWN")) {
            height = selectedShipSize * 40;
        } else {
            width = selectedShipSize * 40;
        }

        Rectangle shipRectangle = new Rectangle(width, height);
        shipRectangle.setFill(shipImageMap.get(selectedShip));

        // Ajustar reflejo visual según dirección original (no cambió)
        switch (shipDirection) {
            case "LEFT" -> shipRectangle.setScaleX(-1);
            case "DOWN" -> shipRectangle.setScaleY(-1);
        }

        // Añadir el barco en la posición inicial calculada
        playerGrid.add(shipRectangle, startCol, startRow);

        if (shipDirection.equals("UP") || shipDirection.equals("DOWN")) {
            GridPane.setRowSpan(shipRectangle, selectedShipSize);
        } else {
            GridPane.setColumnSpan(shipRectangle, selectedShipSize);
        }

        // Quitar barco de la flota
        fleetVBox.getChildren().remove(selectedShip);
        shipSizeMap.remove(selectedShip);
        selectedShip = null;

        // Actualizar estado botón jugar
        boolean emptyFleetVBox = fleetVBox.getChildren().stream()
                .noneMatch(n -> n instanceof Rectangle);

        playButton.setDisable(!emptyFleetVBox);

        // Si no quedan más barcos (solo puede quedar el label)
        boolean onlyLabelLeft = fleetVBox.getChildren().stream().allMatch(node -> !(node instanceof Rectangle));
        if (onlyLabelLeft) {
            Node stackPane = fleetVBox.getParent();
            if (stackPane != null && stackPane.getParent() instanceof HBox gridsHBox) {
                gridsHBox.getChildren().remove(stackPane);
                gridsHBox.setAlignment(Pos.CENTER);
            }
        }
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