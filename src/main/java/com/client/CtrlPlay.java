package com.client;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CtrlPlay implements Initializable {

    @FXML
    public Label txtJugador;

    @FXML
    public GridPane gridSudoku;

    @FXML
    public VBox listaJugadores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        crearTablero();
    }

    private void crearTablero() {

        for (int fila = 0; fila < 9; fila++) {

            for (int columna = 0; columna < 9; columna++) {

                Button casilla = new Button();

                casilla.setPrefWidth(40);
                casilla.setPrefHeight(40);

                gridSudoku.add(casilla, columna, fila);
            }
        }
    }
}