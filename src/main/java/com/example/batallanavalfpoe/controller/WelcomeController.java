package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.model.PlainTextFileHandler;
import com.example.batallanavalfpoe.model.Player;
import com.example.batallanavalfpoe.view.CharacterSelectorStage;
import com.example.batallanavalfpoe.view.GameStage;
import com.example.batallanavalfpoe.view.RulesStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {
    //Atributo de la clase WelcomeController
    //necesitamos tener el plaintexthandler
    PlainTextFileHandler plainTextFileHandler = new PlainTextFileHandler();



    //juan: Esta clase tendra 3 buttons, es una bobada pero por orden voy a modificar el
    /*nombre de un boton, y tambien para seguir mejor el MVc voy a cmabiar como se cargan
    los archivos.
    * */
    //metodos de la clase WelcomeController
    //Button "Jugar"
    @FXML
    private void goToCharacterSelector(ActionEvent event) throws IOException {
        CharacterSelectorStage characterstage = new CharacterSelectorStage();
        characterstage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    //Button "Continuar"
    /*
     * Aca se hace a lo ultimo la implementacion de los archivos planos y serializables
     */
    @FXML
    private void handleClickContinue(ActionEvent event) throws IOException {
        String[] data = plainTextFileHandler.readFromFile("player_data.csv");
        String playerName = data[0]; //nombre del usuario
        String characterImagePath = data[1]; //OJO RUTAAA de imagen del usuario

        Player player = new Player(playerName, characterImagePath);
        System.out.println(playerName + "  ,  " + characterImagePath);

        //aca nos "metemos" en la partida
        //(ojo vivo, sin serializable esto es nada mas disimular q se guarda asjdkasd)
        GameStage gameStage =  new GameStage(new Image(getClass().getResourceAsStream(characterImagePath)), playerName);
        gameStage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }



    //Button "Reglas"
    @FXML
    private void goToRules(ActionEvent event) throws IOException {
        RulesStage rulesstage = new RulesStage();
        rulesstage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
