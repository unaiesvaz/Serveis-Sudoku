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

    private final int[][] tableroInicial = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},

        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},

        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

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

                aplicarBorde(casilla, fila, columna);

                final int filaActual = fila;
                final int columnaActual = columna;

                if (tableroInicial[fila][columna] != 0) {

                    casilla.setText(
                        String.valueOf(tableroInicial[fila][columna])
                    );

                    casilla.setDisable(true);

                } else {

                    casilla.setOnAction(e -> introducirNumero(
                            casilla,
                            filaActual,
                            columnaActual
                    ));
                }

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
                    casilla.setText(String.valueOf(numero));
                    Main.enviarIntento(fila, columna, numero);
                }

            } catch (NumberFormatException ex) {

                System.out.println("Debes introducir un número.");
            }
        });
    }

    public void resultadoIntento(int fila, int columna, int numero, boolean correcto) {

    Button casilla = casillas[fila][columna];

    if (correcto) {
        casilla.setText(String.valueOf(numero));
        casilla.setStyle("-fx-background-color: green;");
        casilla.setDisable(true);
    }
    }

    private void aplicarBorde(Button casilla, int fila, int columna) {

        int arriba = 1;
        int derecha = 1;
        int abajo = 1;
        int izquierda = 1;

        if (fila == 2 || fila == 5) {
            abajo = 3;
        }

        if (columna == 2 || columna == 5) {
            derecha = 3;
        }

        if (fila == 0) {
            arriba = 2;
        }

        if (columna == 0) {
            izquierda = 2;
        }

        if (fila == 8) {
            abajo = 2;
        }

        if (columna == 8) {
            derecha = 2;
        }

        casilla.setStyle(
            "-fx-border-color: black;" +
            "-fx-border-width: " +
            arriba + " " +
            derecha + " " +
            abajo + " " +
            izquierda + ";"
        );
    }
}