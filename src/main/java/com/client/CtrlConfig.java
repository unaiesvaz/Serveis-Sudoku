package com.client;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CtrlConfig { 

    @FXML
    public TextField txtServidor;

    @FXML
    public TextField txtPuerto;

    @FXML
    public TextField txtJugador;

    @FXML
    public Label labelConexion;


    @FXML
    private void connectToServer() { //Listener del boton 
        Main.connectToServer();
    }
}
