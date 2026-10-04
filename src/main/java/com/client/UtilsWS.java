package com.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class UtilsWS { //Esta clase es la conexion con el websocket

    private static UtilsWS sharedInstance = null;

    private WebSocketClient client;

    private Consumer<String> onOpenCallBack = null;
    private Consumer<String> onMessageCallBack = null;
    private Consumer<String> onCloseCallBack = null;
    private Consumer<String> onErrorCallBack = null;

    private String location = ""; //Guarda la direccion del servidor 

    private UtilsWS(String location) {

        this.location = location;

        createNewWebSocketClient();
    }

    private void createNewWebSocketClient() { //Con este metodo, creamos el websocket

        try {

            this.client = new WebSocketClient(
                    new URI(location),
                    new Draft_6455()
            ) {

                @Override
                public void onOpen(ServerHandshake handshake) {

                    String message = "WS connected to: " + getURI();

                    System.out.println(message);

                    if (onOpenCallBack != null) {
                        onOpenCallBack.accept(message);
                    }
                }

                @Override
                public void onMessage(String message) {

                    if (onMessageCallBack != null) {
                        onMessageCallBack.accept(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {

                    String message =
                            "WS closed connection from: "
                            + getURI()
                            + " with reason: "
                            + reason;

                    System.out.println(message);

                    if (onCloseCallBack != null) {
                        onCloseCallBack.accept(message);
                    }
                }

                @Override
                public void onError(Exception e) {

                    String message =
                            "WS connection error: "
                            + e.getMessage();

                    System.out.println(message);

                    if (onErrorCallBack != null) {
                        onErrorCallBack.accept(message);
                    }
                }
            };

            this.client.connect();

        } catch (URISyntaxException e) {

            e.printStackTrace();

            System.out.println(
                    "WS Error, "
                    + location
                    + " is not a valid URI"
            );
        }
    }

    public static UtilsWS getSharedInstance(String location) {

        if (sharedInstance == null) {
            sharedInstance = new UtilsWS(location);
        }

        return sharedInstance;
    }

    public void onOpen(Consumer<String> callBack) { //Cuando nos conectamos al servidor 
        this.onOpenCallBack = callBack;
    }

    public void onMessage(Consumer<String> callBack) { // Cuando el servidor nos manda un mensaje 
        this.onMessageCallBack = callBack;
    }

    public void onClose(Consumer<String> callBack) { // Cuando se cierra la conexion 
        this.onCloseCallBack = callBack;
    }

    public void onError(Consumer<String> callBack) { //Cuando ocurre cualquier error relacionado con la conexion
        this.onErrorCallBack = callBack;
    }

    public void safeSend(String text) { //Para enviar un "mensaje" 

        try {

            if (client != null && client.isOpen()) {

                client.send(text);

            } else {

                System.out.println(
                        "WS Error: Client is not connected."
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "WS Error sending message: "
                    + e.getMessage()
            );
        }
    }

    public void forceExit() {

        System.out.println("WS Closing ...");

        try {

            if (client != null && !client.isClosed()) {
                client.closeBlocking();
            }

        } catch (Exception e) {

            System.out.println(
                    "WS Interrupted while closing WebSocket connection: "
                    + e.getMessage()
            );

            Thread.currentThread().interrupt();
        }
    }

}