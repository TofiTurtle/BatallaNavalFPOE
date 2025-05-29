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

    /*Ya pa terminar,procedimiento pa guardar la partida*/
    //instanciamos el serializable, pq el gamestate es mas abajito en un metodo nuevoo
    private SerializableFileHandler serializableFileHandler;

    //creamos una variable que copie la version del juego a jugar para condicionar el initialize
    private GameState gameState;
    //creemos dos atributos que cuenten los hits para mostrar mensaje de de victoria
    private int playerHits = 0;
    private int machineHits = 0;

    // esto es pa poner la imagen de tocado y de agua (en este caso espacio pq agua no tiene sentido)
    Image hit = new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/hit.png"));
    ImagePattern hitPattern = new ImagePattern(hit);

    Image space = new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/space.png"));
    ImagePattern spacePattern = new ImagePattern(space);

    // clase interna para las excepciones propias del juego
    public class InvalidShipPlacementException extends Exception {
        public InvalidShipPlacementException(String message) {
            super(message);
        }
    }


    //a nuestro atributo gamestate le copiamos el objeto con los datos
    public void getGameState(GameState gameState){
        this.gameState = gameState;
    }

    /*
    Esta funcion recibe como parametro una imagen y l apone en el imageView
    de game
     */
    public void setCharacterImage(Image image) { // excepción no marcada (utilizando null)
        this.pendingCharacterImage = image;
        try {
            if (img != null) {
                img.setImage(image);
            } else {
                throw new NullPointerException("ImageView 'img' no esta inicializado");
            }
        } catch (NullPointerException e) {
            System.err.println("Advertencia: " + e.getMessage());
        }
    }

    /*
    Esta funcion es lo mismo que la anterior pero con el nickname que se le ingresen
     */
    public void setNameLabel(String text) {
        nameLabel.setText(text);
    }

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
        //serialiable siuu siu siu toilet anasdasdas
        serializableFileHandler = new SerializableFileHandler();
    }

    private void winFunction() {
        //FUNCION PARA MOSTRAR MENSAJE DE VICTORIA
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
            opponentButton.setDisable(true);
            opponentButton.setVisible(false);
            buttonsHBox.getChildren().remove(playButton);
            buttonsHBox.getChildren().remove(opponentButton);
            buttonsHBox.setAlignment(Pos.CENTER);
        }
    }
    private void loadSavedGame() {
        playButton.setText("Continuar");//

        System.out.println(">> Cargando partida guardada...");


        //restauramos el titulo del juego
        titleLabel.setText(gameState.getTitleText());

        //copiamos en los tableros las vainas que ya traemos desde el gamestate
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
        //reestablescamos los tiros
        playerHits = gameState.getPlayerShotsSaved();
        System.out.println(playerHits);
        machineHits = gameState.getMachineShotsSaved();
        System.out.println(machineHits);
        winFunction(); //comprobamos si ya gano, pq si si, se bloquea todou


        //le damos la llave de que en este caso ESTA CON UNA PARTIDA INICIADA OJO
        OpponentController.setRestoredFromSavedGame(true);

        //creamos la ventana emergente para ver barcos de machine. le pasamos datos de la machine
        opponentStage = new OpponentStage();
        opponentStage.getController().restoreFrom(
                gameState.getOccupiedMachineCells(),
                gameState.getMachineShips()
        );


        //RESTAURACION DEL JUGADOR:**************************************************
            //restauracion de las cells y su eventos
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                final int r = row;
                final int c = col;

                Rectangle cell = playerBoard.createCell();
                cell.setOnMouseClicked(e -> handlePlayerGridClick(e, r, c));
                cell.setStyle("-fx-background-color: TRANSPARENT;");
                playerGrid.add(cell, c, r); // Primero las celdas
            }
        }
            //restauracion de las imagenes y los barcos del estado de juego :v
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                //en dado caso de que en esa posicion no haya nada, se coloac esto para evitar un NUllpointer exception
                if (playerBoard.getShips()[row][col] == null) continue;

                // solo pintar el barco si es su celda inicial, (evita el apilamiento)
                if (playerBoard.getShips()[row][col].getRow() == row && playerBoard.getShips()[row][col].getCol() == col) {

                    double width = 40;
                    double height = 40;
                    boolean vertical = playerBoard.getShips()[row][col].getDirection().equals("UP") || playerBoard.getShips()[row][col].getDirection().equals("DOWN");

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

                    playerGrid.add(rect, playerBoard.getShips()[row][col].getCol(), playerBoard.getShips()[row][col].getRow());

                    if (vertical) {
                        GridPane.setRowSpan(rect, playerBoard.getShips()[row][col].getSize());
                    } else {
                        GridPane.setColumnSpan(rect, playerBoard.getShips()[row][col].getSize());
                    }
                    rect.toFront(); //las traemos al frente, para que no queden "detras" de la grilla

                    /* tuve q crear esto aca a lo ultimo para q la imagen de tocado y eso se pusieran encima
                    del barco, este for es solo para agregar las imagenes de tocado al tablero del jugador
                     */
                    for (int rows = 0; rows < 10; rows++) {
                        for (int cols = 0; cols < 10; cols++) {
                            //ponemos lo de tocado y demas en el tablero del jugador
                            if (playerBoard.getshotsOnterritory(rows, cols)) {
                                Rectangle effect = new Rectangle(40, 40);
                                if (playerBoard.isOccupied(rows, cols)) {
                                    effect.setFill(hitPattern);
                                } else {
                                    effect.setFill(spacePattern);
                                }
                                playerGrid.add(effect, cols, rows);
                                effect.toFront(); // la imagen se pone encima del barco
                            }
                        }
                    }
                }
            }
        }
        //FIN DE RESTAURACION DEL JUGADOR

        //RESTAURACION DEL TABLERO DE LA MAQUINA
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = opponentBoard.createCell();
                opponentGrid.add(cell, col, row);

                //colorear las celdas del oponente
                if (opponentBoard.getshotsOnterritory(row,col)) {
                    if (opponentBoard.isOccupied(row,col)) {
                        cell.setFill(hitPattern); // impacto
                    } else {
                        cell.setFill(spacePattern); // agua
                    }
                }
            }
        }
        //FIN RESTAURACION MACHINE

        //le volvemos a dar vaina de los eventos
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

        //aca creamos las listas con los barcos, para poder sacar los barcos q falten poner
        //y con ello, llenar el fleetbox
        List<Ship> fullFleet = generateFullFleet();
        List<Ship> placedShips = getPlacedShips(playerBoard.getShips());
        List<Ship> remainingShips = calculateRemainingShips(fullFleet, placedShips);
        fillFleetBox(fleetVBox, remainingShips);

        //Ahora, un condicional para saber cuando el boton de jugar esta habilitado (si no hay barcos restantes)
        if(remainingShips.isEmpty()) {
            //no hace falta habilitar oButton, ya se hace en el playB, aca tambien se muestra asi q parchese
            playButton.setDisable(false);//quitamos el boton de mientras, se activa cuando esten todos lso barcos
        }else{
            opponentButton.setDisable(true);//ojo vivo, se debe mantener desabilitado
            playButton.setDisable(true);//quitamos el boton de mientras, se activa cuando esten todos lso barcos
        }

        // Centrar botones del HBox
        buttonsHBox.setAlignment(Pos.CENTER);
        //asdasd

        // Si ya no quedan barcos, quitar fleetVBox y centrar las grillas
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

    //*****************************************************************
    //esta funcion llena una lista con los barcos disponibles en el juego
    private List<Ship> generateFullFleet() {
        List<Ship> fleet = new ArrayList<>();

        for (int i = 0; i < 1; i++) fleet.add(new Ship(4, "Portaviones", 0,"default"));
        for (int i = 0; i < 2; i++) fleet.add(new Ship(3, "Submarino", 0,"default"));
        for (int i = 0; i < 3; i++) fleet.add(new Ship(2, "Destructor", 0,"default"));
        for (int i = 0; i < 4; i++) fleet.add(new Ship(1, "Fragata", 0,"default"));
        return fleet;
    }
    //ahora, esta funcion lo que ahce es llenar un arreglo con los barcos que ESTAN COLOCADOS
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
    //ahora, esta funcion lo que hace es recorrer las listas, si encuentra un barco que SI esta colocado
    //lo que hace es sacarlo de la lista (originalmente era flotacompltea). dejando una lista de solo lo q falta
    private List<Ship> calculateRemainingShips(List<Ship> fullFleet, List<Ship> placedS) {
        List<Ship> remaining = new ArrayList<>(fullFleet); // copiar

        for (Ship placed : placedS) {
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i).getSize() == placed.getSize()) {
                    remaining.remove(i); // elimina solo una ocurrencia
                    break;
                }
            }
        }
        return remaining;
    }
    //por ultimo, en esta funcion con los barcos faltantes obtenidos anteriormente, llenamos el fleetbox
    //para casos de partidas donde falte por colocar barcos. (guardamos con cada evento click en gridPlayer)
    private void fillFleetBox(VBox fleetVBox, List<Ship> remaining) {
        fleetVBox.getChildren().clear();
        //mostramos la etiqueta de barcos resultantes, la traemos al frenet
        fleetLabel.setVisible(true);
        fleetVBox.getChildren().add(fleetLabel);


        for (Ship ship : remaining) {
            int size = ship.getSize();
            Rectangle rect = new Rectangle(size * 40, 40);

            // Asignar imagen según el tamaño
            ImagePattern pattern = switch (size) {
                case 1 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/frigate_right.png")));
                case 2 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/destroyer_right.png")));
                case 3 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/submarine_right.png")));
                case 4 -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/carrier_right.png")));
                default -> new ImagePattern(new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/default_right.png")));
            };

            rect.setFill(pattern);

            // Estas tres líneas copian el comportamiento de newgame:
            shipSizeMap.put(rect, size);
            shipImageMap.put(rect, pattern);
            rect.setOnMouseClicked(event -> selectShip(rect));
            fleetVBox.getChildren().add(rect);
        }

    }
    //**************************************************************************************


    private void setupNewGame() {
        saveGame();//ojo vivo, toca guardar partida aqui para que no pase bug q menciono valeria
        /*
        Se crea un opponent stage debido a como valeria crea los barcos del oponente,
        ella los crea en un stage totalmente diferente al gridpane del oponente (el principal)
        y los crea en otro stage, entonces al momento de iniciar el gamestage creo una instancia de opponent para
        crear los barcos de una
         */
        OpponentController.setRestoredFromSavedGame(false);
        opponentStage = new OpponentStage();

        /*
        Se desactiva el gridpane del oponente mientras y tambien desactiva el boton que muestra el stage donde
        se encuentran los barcos del oponente
         */
        deactivateGrid(opponentGrid);
        opponentButton.setDisable(true);

        /*
        se llama la funcion que copia los barcos creados en el stage del oponente (RECORDAR QUE LOS BARCOS NO
        SE CREAN DIRECTAMENTE EN EL GRIDPANE PRINCIPAL DEL ENEMIGO)
         */
        copyOpponentShips();

        if (pendingCharacterImage != null) {
            img.setImage(pendingCharacterImage);
        }

         /*
         Aqui puede parece confunso por como se declaran las variables pero simplemente se estan creando rectangulos
         se le puso el nombre de cells ya que simula una celda del tablero, pero en escencia
         es un rectangulo dentro de cada gridpane, a demas se le asigna un evento de clic
         */
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

         /*
        tenemos que retomar la idea de que los nodos son elemtos visuales, entonces que hacemos aca, aca recorremos el vbox donde
        se encuentran los rectangulos, o sea, lo que dice la linea del for es: por cada nodo (elemento visual) que se encuentre
        en el fleetvbox se va poner la imagen y aparte se pone un evento de clic al rectangulo (el evento de selected)
         */
        for (Node child : fleetVBox.getChildren()) {
            if (child instanceof Rectangle rect) { //aqui se pregunta si el nodo que se encuentra en el fleetvbox es un rectangulo y se guarda ese rectangulo en una variable
                int size = (int) (rect.getWidth() / 40); //se divide el largo del rectangulo entre 40 (debido a que ese es el tamaño de cada lado de una celda)
                shipSizeMap.put(rect, size); //se guarda el rectangulo y el numero que me dio la division

                //dependiendo del numero de la division se asigna una imagen
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

        /*
        Aqui se le pone de evento al stack pane (recordar que este contiene: el gridpane, el rectangulo que simula la celda,
        el barco) y se asigna a un string la dirrecion dependiendo de la tecla que undiste
         */
        playerGridContainer.setOnKeyPressed(event -> {
            if (selectedShip == null) return;
            switch (event.getCode()) {
                case UP -> shipDirection = "UP";
                case DOWN -> shipDirection = "DOWN";
                case LEFT -> shipDirection = "LEFT";
                case RIGHT -> shipDirection = "RIGHT";
            }
        });

        /*
        segun entendi esto es para que depues de que la interfaz este lista ya se puedan recibir ventos
         */
        Platform.runLater(() -> {
            playerGridContainer.requestFocus();
            playerGridContainer.setFocusTraversable(true);
        });

        playButton.setDisable(true);
    }

    /*
    funcion la cual recibe como parametro un recntagulo y lo que hace es cambiar el borde del rectangulo
    asignar la dirrecion
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

    //esto maneja los tiros del jugador en <<el machine grid>>, por eso el nombre de ese metodo
    private void handleMachineGridClick(MouseEvent event, int row, int col) {
        if(gridDisabled) return; //si disabled, faltan barcos, no haga nada
        if(!shootingTurn) return; //si no tiene el turno, salga (aunque esto nunca ejecuta tecnicamente)

        //inicializamos variables
        int shotRow = row;
        int shotCol = col;

        //Valida ahi breve que el tiro que se quiere hacer SI este en la grilla, sino se cancela
        if (!opponentBoard.isWithinBounds(shotRow, shotCol))
            return;

        //aqui haria el playerShot(row,col) pa guardar el tiro
        /*ignorar esto, esto lo dejo aca pq seria necesario para serializar mas tarde*/

        //creamos la figura del rectangulo para simular graficamente el tiro
        double width = 40;
        double height = 40;
        Rectangle shotRectangle = new Rectangle(width, height);
        shotRectangle.toFront(); // la imagen se pone encima del barco

        //lo mostramos en el opponent grid
        opponentGrid.add(shotRectangle, shotCol, shotRow);
        //Y tambien, ahora copiemoslo en la matriz de tiros bool del opponenBoardo!
        opponentBoard.setShotsOnterritory(shotRow, shotCol); //tripi


        //ahora hagamos la respectiva comprobacion de hit o miss
        if (opponentBoard.isOccupied(shotRow, shotCol)) {
            //registramos el disparo de el jugador
            playerHits++;
            System.out.println(playerHits);
            shotRectangle.setFill(hitPattern); // se pone la imagen de q se toco
            winFunction();//llamamos condicion de victoria

            // 1. Obtener el barco que fue impactado
            Ship hitShip = opponentBoard.getShip(shotRow, shotCol); // esto debes implementarlo

            // 2. Registrar el impacto
            hitShip.registerHit();

            // 3. ¿Está hundido?
            if (hitShip.getHits() >= hitShip.getSize()) { //Si hay IGUAL O MAS HITS QUE SU TAMAÑO es q lo hundieron
                System.out.println("HUNDIDO!!! 🔥 El " + hitShip.getName() + " ha sido destruido por el JUGADOR.");

            } else {
                System.out.println("TOCADO!!! 💥 Al " + hitShip.getName() + " Haz acertado tu Tiro! intente de nevo");

            }
            saveGame();
            shootingTurn = true; //sigue teniendo el turno, puede acceder al evento again

        } else {
            System.out.println("MISS!!!! awwww------------------");
            shotRectangle.setFill(spacePattern); // se pone la imagen de espacio si fallo
            saveGame();
            shootingTurn = false; //pierde el turno
            opponentGrid.setDisable(true); //hacemos esto para que el jugador NO SIGA TIRANDO A QUEMARROPA. falla->bloqueamos
            processMachineShot(); //llama a la maquina para que tire
        }

    }

    private void processMachineShot() {
        /*en escensia pausetransicion es una clase diseñada literal para "congelar" procesos
        del programa, NO los congela, da una ilusion
        Setonfinish.pause dice QUE COSAS VA A EJECUTAR despues del tiempo de pausa (1500ms)
         lo q este dentro de esos { es lo q se va a retrasar el x tiempo*/
        shootingTurn = false;// ---> turno de la maquina

        PauseTransition thinkingPause = new PauseTransition(Duration.millis(1500));
        thinkingPause.setOnFinished(e -> {
            //creamos el tiro de la maquina de manera aleatoria
            Random random = new Random();
            int MachineshotRow = random.nextInt(10);
            int MachineshotCol = random.nextInt(10);

            //aca creamos el respectivo rectanuglo para simular el tiro de maquina
            double width = 40;
            double height = 40;
            Rectangle machineShotRectangle = new Rectangle(width, height);

            //colocamos en nuestro playergrid donde cayo el tiro, para corroborar q si se hizo
            playerGrid.add(machineShotRectangle, MachineshotCol, MachineshotRow);
            //Y por aca tambien, colocamos los disparos de la maquina en nuestra matriz de playerbord
            playerBoard.setShotsOnterritory(MachineshotRow, MachineshotCol); //tropi

            //condicional para comprobar x2 si el comportamiento es adecuado + salir del dowhile
            if(playerBoard.isOccupied(MachineshotRow, MachineshotCol)) {
                //registramos el disparo acertado de la machin
                machineHits++;
                System.out.println(machineHits);
                machineShotRectangle.setFill(hitPattern); // se pone la imagen de q se toco
                winFunction();

                // 1. Obtener el barco que fue impactado
                Ship hitShip = playerBoard.getShip(MachineshotRow, MachineshotCol); //se crea barco tocado con el barco de el PLAYERboard ojo vivo, es del player

                // 2. Registrar el impacto
                hitShip.registerHit();

                // 3. ¿Está hundido?
                if (hitShip.getHits() >= hitShip.getSize()) { //Si hay IGUAL O MAS HITS QUE SU TAMAÑO es q lo hundieron
                    System.out.println("HUNDIDO!!! 🔥 El " + hitShip.getName() + " ha sido destruido por la MAQUINA.");

                } else {
                    System.out.println("TOCADO!!! 💥 Al " + hitShip.getName() + " lo ha tocado la MAQUINA.");
                }

                saveGame(); //OJO VIVITO; GUARDAMOS LA PARTIDA AQUI; DESPUES DE HACER TIRO ACERTADO
                processMachineShot(); //llamamos recursivamente, por problema de bucles, a que maquina siga tirando
            }else {
                System.out.println("La maquina FALLO");
                machineShotRectangle.setFill(spacePattern); // se pone la imagen de espacio si fallo
                saveGame(); //OJO VIVITO: SE GUARDA LA PARTIDA TAMBIEN POR SI LA MACHINE FALLA
                opponentGrid.setDisable(false); //si la maquina falla, volvemos a activar Ogrid pa que siga tirando

            }
        });
        thinkingPause.play(); //ejecutamos la vaina que quede pensando

        //si sale del dowhhile es que fallo, entonces si fallo se le devuelve el turno a player
        shootingTurn = true;

    }
    //metodo temporal para comprobar que si se genera esa webada bien
    //listo, funciona bien en opponent y player


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

        // Validar colocación usando el metodo del modelo
        try { // Uso de excepción propia al colocar barcos
            if (!playerBoard.canPlaceShip(startRow, startCol, selectedShipSize, shipDirection)) {
                throw new InvalidShipPlacementException("No se puede colocar el barco aqui.");
            }
        } catch (InvalidShipPlacementException e) {
            System.err.println("Error: " + e.getMessage()); // EXCEPCION SI NO SE PUEDE PONER BARKO
            return;
        }

        //mini funcion para darle nombre
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

        Ship ship = new Ship(selectedShipSize, shipName, 0,shipDirection);
        // Colocar barco en el modelo
        playerBoard.placeShip(startRow, startCol,ship,shipDirection);


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

        //se pone visualmente en el grid
        playerGrid.add(shipRectangle, startCol, startRow);
        saveGame();//guardamos partida

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
        // 1. Cambiar el tipo de List<OpponentController.Ship> a List<Ship>
        List<Ship> placedShips = OpponentController.getSavedPlacedShips();
        if (placedShips == null) return;

        // 2. Copiar datos al opponentBoard (modelo)
        for (Ship ship : placedShips) {
            opponentBoard.placeShip(
                    ship.getRow(),
                    ship.getCol(),
                    ship,
                    ship.getDirection()
            );
        }

        // 3. Renderizado visual (igual que antes, pero usando Ship en lugar de OpponentController.Ship)
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

    /*
    crea rectangulos con eventos de mouse
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

        // cambiaos el etxto pa q ya no se vea pon tus flotas
        titleLabel.setText("que la fuerza te acompañe...");
        fleetLabel.setVisible(false); //quitamos el label que muestra en el fleetbox
        winFunction(); //por si ya gano, pues muestre

        // actualizamos el titulo en gamestate
        if (gameState != null) {
            gameState.setTitleText(titleLabel.getText());
        }

        // GUARDAR POR SI LAS MOSCAS
        saveGame();
    }


    @FXML
    private void goToWelcomeStage(ActionEvent event) { //  Excepciones marcadas al cambiar de ventana
        try {
            new WelcomeStage().show();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            opponentStage.close();
        } catch (IOException e) {
            System.err.println("Error al abrir la pantalla de bienvenida: " + e.getMessage()); // usamos err pa q se vea rojo
        }
    }

    @FXML
    private void showOpponentBoard(ActionEvent event) throws IOException {
        opponentStage.show();
    }

    //Ultimo metodo de la logica, si señor, metodo para guardar la partida siuu
    private void saveGame(){
        //para el pleyer
        Ship[][] playerShips = playerBoard.getShips();
        boolean[][] playerShots = playerBoard.getShotsBoard();
        boolean[][] occupiedPlayerCells = playerBoard.getOccupiedCells();

        //para el Machin
        Ship[][] machineShips = opponentBoard.getShips();
        boolean[][] machineShots = opponentBoard.getShotsBoard();
        boolean[][] occupiedMachineCells = opponentBoard.getOccupiedCells();

        //tenemos que guardar tambien los estados
        int playerShotSaved = playerHits;
        int machineShotSaved = machineHits;
        int[] shotsData = {playerShotSaved, machineShotSaved};

        //ahora si, creamos el objeto gamestate, pues ya tenemos listos sus atributicos
        GameState gameState = new GameState(playerShips, playerShots,occupiedPlayerCells, machineShips, machineShots,occupiedMachineCells, titleLabel.getText(),shotsData);

        //por ultimito, sencillamente le pasamos nuestro estado del juego al papuserializador
        serializableFileHandler.serialize("game_data.ser", gameState);

        System.out.println("Si se guardo manito, calma! :)))");

        /*Esta vaina tecnicamnte si queremos lo podriamos hacer con un boton, yo quiero
        * que sea un salvado automatico, entonces lo colocamos despues de realizar cada shoto*/
    }
}
