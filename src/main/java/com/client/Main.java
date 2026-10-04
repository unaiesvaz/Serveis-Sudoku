package com.client;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application { //.\run.ps1 com.client.Main

    public static CtrlConfig ctrlConfig;
    public static CtrlPlay ctrlPlay;
    public static CtrlScore ctrlScore;

    public static UtilsWS wsClient;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        final int windowWidth = 1000;
        final int windowHeight = 700;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");

        UtilsViews.addView(
            getClass(),
            "ViewConfig",
            "/assets/viewConfig.fxml"
        );

        UtilsViews.addView(
            getClass(),
            "ViewPlay",
            "/assets/viewPlay.fxml"
        );

        UtilsViews.addView(
            getClass(),
            "ViewScore",
            "/assets/viewScore.fxml"
        );

        ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");
        ctrlPlay = (CtrlPlay) UtilsViews.getController("ViewPlay");
        ctrlScore = (CtrlScore) UtilsViews.getController("ViewScore");

        Scene scene = new Scene(UtilsViews.parentContainer);

        stage.setScene(scene);
        stage.setTitle("Sudoku Online");
        stage.setMinWidth(windowWidth);
        stage.setMinHeight(windowHeight);
        stage.show();
    }

    public static void connectToServer() {

    String servidor = ctrlConfig.txtServidor.getText();
    String puerto = ctrlConfig.txtPuerto.getText();
    String jugador = ctrlConfig.txtJugador.getText();

    String serverUri = "ws://" + servidor + ":" + puerto;

    ctrlConfig.labelConexion.setText("Conectando...");

    wsClient = UtilsWS.getSharedInstance(serverUri);

    wsClient.onOpen((message) -> {

        Platform.runLater(() -> {
            ctrlConfig.labelConexion.setText("Conectado como " + jugador);
        });

        JSONObject obj = new JSONObject();

        obj.put("type", "join");
        obj.put("name", jugador);

        wsClient.safeSend(obj.toString());
    });

    wsClient.onMessage((message) -> {

        JSONObject obj = new JSONObject(message);

        String type = obj.getString("type");

        if (type.equals("join_ok")) {

            Platform.runLater(() -> {

                ctrlPlay.txtJugador.setText("Jugador: " + jugador);

                UtilsViews.setView("ViewPlay");
            });
        } else if (type.equals("players")) {

            JSONArray playersArray = obj.getJSONArray("players");

            Platform.runLater(() -> {
                ctrlPlay.mostrarJugadores(playersArray);
                ctrlScore.mostrarJugadores(playersArray);
            });
        } else if (type.equals("guess_result")) {

            int fila = obj.getInt("fila");
            int columna = obj.getInt("columna");
            int numero = obj.getInt("numero");
            boolean correcto = obj.getBoolean("correcto");

            Platform.runLater(() -> {
                ctrlPlay.resultadoIntento(fila,columna,numero,correcto);
            });
        } else if (type.equals("game_finished")) {

            Platform.runLater(() -> {

                UtilsViews.setView("ViewScore");
            });
        } else if (type.equals("board_state")) {

            JSONArray casillasArray = obj.getJSONArray("casillas");

            Platform.runLater(() -> {
                ctrlPlay.sincronizarTablero(casillasArray);
            });
        }
        });

        wsClient.onError((message) -> {

            Platform.runLater(() -> {ctrlConfig.labelConexion.setText("Error de conexión");});
        });

    wsClient.onClose((message) -> {

        Platform.runLater(() -> {
            ctrlConfig.labelConexion.setText("Desconectado");
        });
    });
    }

    @Override
    public void stop() {

        if (wsClient != null) {
            wsClient.forceExit();
        }
    }

    public static void enviarIntento(int fila, int columna, int numero) {

        JSONObject obj = new JSONObject();

        obj.put("type", "guess");
        obj.put("fila", fila);
        obj.put("columna", columna);
        obj.put("numero", numero);

        wsClient.safeSend(obj.toString());
    }
}