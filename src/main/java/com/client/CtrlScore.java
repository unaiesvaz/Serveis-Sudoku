package com.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CtrlScore implements Initializable {

    @FXML
    public VBox listaJugadores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void mostrarJugadores(JSONArray playersArray) {

        listaJugadores.getChildren().clear();

        ArrayList<JSONObject> jugadores = new ArrayList<>();

        for (int i = 0; i < playersArray.length(); i++) {

            jugadores.add(playersArray.getJSONObject(i));
        }

        jugadores.sort(
            Comparator.comparingInt((JSONObject jugador) -> jugador.getInt("score")).reversed()
        );

        for (JSONObject jugador : jugadores) {

            String name = jugador.getString("name");
            int score = jugador.getInt("score");

            Label label = new Label(
                    name + " - " + score + " puntos"
            );

            listaJugadores.getChildren().add(label);
        }
    }

    @FXML
    private void volverAJugar() {

        UtilsViews.setView("ViewPlay");
    }
}