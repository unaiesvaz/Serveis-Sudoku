package com.client;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CtrlConfig implements Initializable { 

    @FXML
    public TextField txtServidor;

    @FXML
    public TextField txtPuerto;

    @FXML
    public TextField txtJugador;

    @FXML
    public Label labelConexion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void connectToServer() { //Listener del boton 
        labelConexion.setText("Conectando...");
    }
}
