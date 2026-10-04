package com.client;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CtrlPlay implements Initializable {

    @FXML
    public Label txtJugador;

    @FXML
    public GridPane gridSudoku;

    @FXML
    public VBox listaJugadores;

    private Button[][] casillas = new Button[9][9];

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

                final int filaActual = fila;
                final int columnaActual = columna;

                casilla.setOnAction(e -> introducirNumero(
                        casilla,
                        filaActual,
                        columnaActual
                ));

                casillas[fila][columna] = casilla;

                gridSudoku.add(casilla, columna, fila);
            }
        }
    }

    public void mostrarJugadores(JSONArray playersArray) {

        listaJugadores.getChildren().clear();

        for (int i = 0; i < playersArray.length(); i++) {

            JSONObject playerObj = playersArray.getJSONObject(i);

            String name = playerObj.getString("name");
            int score = playerObj.getInt("score");

            Label jugador = new Label(name + " - " + score + " puntos");

            listaJugadores.getChildren().add(jugador);
        }
    }

    private void introducirNumero(Button casilla, int fila, int columna) {

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Sudoku");
        dialog.setHeaderText("Casilla: fila " + fila + ", columna " + columna);
        dialog.setContentText("Introduce un número del 1 al 9:");

        dialog.showAndWait().ifPresent(valor -> {

            try {

                int numero = Integer.parseInt(valor);

                if (numero >= 1 && numero <= 9) {
                    Main.enviarIntento(fila, columna, numero);

                }

            } catch (NumberFormatException ex) {

                System.out.println("Debes introducir un número.");
            }
        });
    }

    public void resultadoIntento(int fila, int columna, boolean correcto) {

    Button casilla = casillas[fila][columna];

    if (correcto) {

        casilla.setText("✓");
        casilla.setDisable(true);

    }
    }
}