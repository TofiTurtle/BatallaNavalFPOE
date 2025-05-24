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

    Font baseFont = Font.loadFont(getClass().getResourceAsStream("/com/example/batallanavalfpoe/fonts/Strjmono.ttf"), 25);

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

    /*Ya pa terminar,procedimiento pa guardar la partida*/
    //instanciamos el serializable, pq el gamestate es mas abajito en un metodo nuevoo
    private SerializableFileHandler serializableFileHandler;

    //creamos una variable que copie la version del juego a jugar para condicionar el initialize
    private GameState gameState;

    /*OJO VIVO; nuevo metodo necesario para que el programa vea y entienda que version se jugara
     * si se juega una version ya iniciada, o si una nueva partida, para esto necesitaremos un metodo extras*/
    /*public void gameVersion(GameState gameState) {
        if (gameState == null)
        {
            System.out.println("ESTA JUGANDO DESDE 0-----------------");

        }else{
            System.out.println("ESTA JUGANDO UNA PARTIDA YA INICIADA++++++++++++");
            System.out.println("OJO VIVO JUGANDO UNA PARTIDA YA INICIADA");
            playerBoard.restoreBoard(gameState.getPlayerShips(), gameState.getPlayerShots(), gameState.getOccupiedPlayerCells());
            opponentBoard.restoreBoard(gameState.getMachineShips(), gameState.getMachineShots(), gameState.getOccupiedMachineCells());

            //SUPUESTAMENTE Y SI SI SE GUARDO LA PARTIDA, AHORA SI IMPRIMO EL GAMEBOARD, ME DEBERIA DE MOSTRAR UNA VAINA ACORDE
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    System.out.printf("%-3s ", playerBoard.getshotsOnterritory(i, j)); // %-3s = 3 caracteres de ancho, alineado a la izquierda
                }
                System.out.println();
            }
        }
    }*/

    //a nuestro atributo gamestate le copiamos el objeto con los datos
    public void getGameState(GameState gameState){
        this.gameState = gameState;
    }


    /*
    Esta funcion recibe como parametro una imagen y l apone en el imageView
    de game
     */
    public void setCharacterImage(Image image) {
        this.pendingCharacterImage = image;
        if (img != null) {
            img.setImage(image);
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

    private void loadSavedGame() {
        /* NO COMENTEN ESTOS METODOS POR QUE ES LO MISMO QUE ESTAMOS HACIENDO EN SETUPNEWGAME
        Y DEJEN DE PONER COMENTARIOS INNECESARIOS PORQUE SINO YA NO VOY A PODER CORRER EL JUEGO, GRACIAS*/

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

        /* ya confirme que todo se esta guardando (modifique temporalmente vermatriztiros para que muestre en
        consola los tiros de la maquina, del oponente, y la ubicacion de los barcos de la maquina y del oponente)
        SIN EMBARGO, al darle en continuar no se muestra nada, porque en este metodo aun no se le asigna ni la imagen
        a los rectangulos ni se han generado los gridpanes como se hace en setupnewgame, por eso es como si
        estuvieramos mostrando solo lo que hay en el fxml, el punto es que SI se estan guardadno las cosas solo q
        pues toca poner eso que esta guardado en restoreboard visualmente
         */
        vermatriztiros();
    }

    private void setupNewGame() {
        /*
        Se crea un opponent stage debido a como valeria crea los barcos del oponente,
        ella los crea en un stage totalmente diferente al gridpane del oponente (el principal)
        y los crea en otro stage, entonces al momento de iniciar el gamestage creo una instancia de opponent para
        crear los barcos de una
         */
        opponentStage = new OpponentStage();

        // Se crean 2 gridPanes de manera dinamica
        playerGrid = new GridPane();
        playerGridContainer.getChildren().add(playerGrid);
        playerBoard.setupGrid(playerGrid);

        opponentGrid = new GridPane();
        mainGridContainer.getChildren().add(opponentGrid);
        opponentBoard.setupGrid(opponentGrid);

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

        ship.setStroke(Color.BLACK);
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
        shotRectangle.setStroke(Color.GREEN); //color vistoso pa confirmar q sise pone

        //lo mostramos en el opponent grid
        opponentGrid.add(shotRectangle, shotCol, shotRow);
        //Y tambien, ahora copiemoslo en la matriz de tiros bool del opponenBoardo!
        opponentBoard.setShotsOnterritory(shotRow, shotCol); //tripi
        vermatriztiros(); //pillemos si esta bien

        //ahora hagamos la respectiva comprobacion de hit o miss
        if (opponentBoard.isOccupied(shotRow, shotCol)) {
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
            machineShotRectangle.setStroke(Color.RED); //color vistoso pa confirmar q sise pone


            //colocamos en nuestro playergrid donde cayo el tiro, para corroborar q si se hizo
            playerGrid.add(machineShotRectangle, MachineshotCol, MachineshotRow);
            //Y por aca tambien, colocamos los disparos de la maquina en nuestra matriz de playerbord
            playerBoard.setShotsOnterritory(MachineshotRow, MachineshotCol); //tropi

            //condicional para comprobar x2 si el comportamiento es adecuado + salir del dowhile
            if(playerBoard.isOccupied(MachineshotRow, MachineshotCol)) {

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
    private void vermatriztiros() {
        System.out.println("\n📍 MATRIZ DE TIROS DEL JUGADOR SOBRE EL OPONENTE:");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.printf("%-3s ", opponentBoard.getshotsOnterritory(i, j)); // %-3s = 3 caracteres de ancho, alineado a la izquierda
            }
            System.out.println();
        }

        System.out.println("\n📍 MATRIZ DE TIROS DEL OPONENTE SOBRE EL JUGADOR:");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.printf("%-3s ", playerBoard.getshotsOnterritory(i, j)); // %-3s = 3 caracteres de ancho, alineado a la izquierda
            }
            System.out.println();
        }

        System.out.println("\n🚢 UBICACION DE BARCOS DEL JUGADOR:");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.printf("%-3s ", playerBoard.getOccupiedCellsPlayer(i,j)); // %-3s = 3 caracteres de ancho, alineado a la izquierda
            }
            System.out.println();
        }

        System.out.println("\n🚢 UBICACION DE BARCOS DEL OPONENTE:");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.printf("%-3s ", opponentBoard.getOccupiedCellsPlayer(i,j)); // %-3s = 3 caracteres de ancho, alineado a la izquierda
            }
            System.out.println();
        }
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

        Ship ship = new Ship(selectedShipSize, shipName, 0);
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

        //ahora si, creamos el objeto gamestate, pues ya tenemos listos sus atributicos
        GameState gameState = new GameState(playerShips, playerShots,occupiedPlayerCells, machineShips, machineShots,occupiedMachineCells);

        //por ultimito, sencillamente le pasamos nuestro estado del juego al papuserializador
        serializableFileHandler.serialize("game_data.ser", gameState);

        System.out.println("Si se guardo manito, calma! :)))");

        /*Esta vaina tecnicamnte si queremos lo podriamos hacer con un boton, yo quiero
        * que sea un salvado automatico, entonces lo colocamos despues de realizar cada shoto*/
    }
}
