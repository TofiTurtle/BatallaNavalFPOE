package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.PlainTextFileHandler;
import com.example.batallanavalfpoe.model.Player;
import com.example.batallanavalfpoe.view.GameStage;
import com.example.batallanavalfpoe.view.WelcomeStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CharacterSelectorController {
    @FXML
    private Button jugarButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Button previousButton, nextButton;

    @FXML
    private TextField textField;

    @FXML
    private Label emptyNameLabel;

    private List<Image> images;
    private int currentIndex = 2;

    /*creo este arreglo pq necesito tener como tal los STRING de los path de imagenes, y ps
    la lista de abajo la unica manera en la que m lo devuelve es en Image. tonces no sirve
    * */
    private String PathListImages[] = { "/com/example/batallanavalfpoe/images/personajeUno.jpg",
                                        "/com/example/batallanavalfpoe/images/personajeDos.jpg",
                                        "/com/example/batallanavalfpoe/images/personajeTres.jpg"
                                      };
    //Por supuesto definmos el texthanlder
    private PlainTextFileHandler plainTextFileHandler;


    @FXML
    private void welcomeStage (ActionEvent event) throws IOException {
        WelcomeStage welcomeStage = new WelcomeStage();
        welcomeStage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }

    @FXML
    public void initialize() {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                emptyNameLabel.setVisible(false); // mira si esta vacio para mostrar el mensaje de trin
            }
        });

        images = List.of(
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/personajeUno.jpg")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/personajeDos.jpg")),
                new Image(getClass().getResourceAsStream("/com/example/batallanavalfpoe/images/personajeTres.jpg"))
        );
        imageView.setImage(images.get(currentIndex));

        //OJO ATENTO, tenemos que crear un objeto global de esta vainosa
        plainTextFileHandler = new PlainTextFileHandler();
        //trin tran
    }

    @FXML
    private void nextImage() {
        if (currentIndex < images.size() - 1) {
            currentIndex++;
            imageView.setImage(images.get(currentIndex));
        } else {
            currentIndex = 0;
            imageView.setImage(images.get(currentIndex));
        }
    }

    @FXML
    private void previousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            imageView.setImage(images.get(currentIndex));

        }else {
            currentIndex = 2;
            imageView.setImage(images.get(currentIndex));
        }
    }

    /*
    MUCHO CUIDADO CON ESTA IMPLEMENTACION HAY QUE TENER CUIDADO PQ HAY CIERTAS COSITAS QUE TODAVIA
    NO ENTIENDO
     */

    @FXML
    private void playButton(ActionEvent event) throws IOException {
        //este pedazo de logica es una tipo excepcion para que si no se ingresa nombre
        //no deje iniciar la aplicacion cierto? see xd
        String name = textField.getText().trim();
        if (name.isEmpty()) {
            emptyNameLabel.setText("Ingrese un nickname");
            emptyNameLabel.setVisible(true);
            return;
        }
        /*cuando se presione jugar en characterStage, se crea una instancia de Jugador
        y ps se crea el content que se le pasa a el plainTexthANLDER
        (de momento nombre e imagen) pq considero que el numero de flotas hundidas
        y eso seria estado de partida actual y pues eso ya iria es en el serializable
        igualmente luego lo añado si quieren :>
        * */
        Player player = new Player(name,PathListImages[currentIndex]);

        String content = player.getPlayerName() + "," + player.getCharacterImagePath();
        //se crea el archivo ese d texto con las vainas nombre y la imagen
        plainTextFileHandler.writeToFile("player_data.csv", content);

        //se instancia geimstage y le pasamos por parametros la imaen y nombre
        GameStage gameStage = new GameStage(images.get(currentIndex), name);
        gameStage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close(); //ojo vivo a esto pq es importante para que se cierre
    }

}
