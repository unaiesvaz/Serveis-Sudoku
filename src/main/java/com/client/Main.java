package com.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application { //.\run.ps1 com.client.Main

    public static CtrlConfig ctrlConfig;

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

        ctrlConfig = (CtrlConfig) UtilsViews.getController("ViewConfig");

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
                ctrlConfig.labelConexion.setText(
                        "Conectado como " + jugador
                );
            });
        });

        wsClient.onError((message) -> {
            Platform.runLater(() -> {
                ctrlConfig.labelConexion.setText(
                        "Error de conexión"
                );
            });
        });

        wsClient.onClose((message) -> {
            Platform.runLater(() -> {
                ctrlConfig.labelConexion.setText(
                        "Desconectado"
                );
            });
        });
    }

    @Override
    public void stop() {

        if (wsClient != null) {
            wsClient.forceExit();
        }
    }
}