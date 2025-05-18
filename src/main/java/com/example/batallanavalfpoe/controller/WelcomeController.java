package com.example.batallanavalfpoe.controller;

import com.example.batallanavalfpoe.view.CharacterSelectorStage;
import com.example.batallanavalfpoe.view.RulesStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {


    //juan: Esta clase tendra 3 buttons, es una bobada pero por orden voy a modificar el
    /*nombre de un boton, y tambien para seguir mejor el MVc voy a cmabiar como se cargan
    los archivos.
    * */

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


    //Button "Reglas"
    @FXML
    private void goToRules(ActionEvent event) throws IOException {
        RulesStage rulesstage = new RulesStage();
        rulesstage.show();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
