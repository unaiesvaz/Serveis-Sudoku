package com.server;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class Main extends WebSocketServer { //.\run.ps1 com.server.Main

    public static final int DEFAULT_PORT = 3000;

    public Main(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Cliente conectado: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Cliente desconectado: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Mensaje recibido: " + message);

        JSONObject obj = new JSONObject(message);

        String type = obj.getString("type");

        if (type.equals("join")) {

            String name = obj.getString("name");

            System.out.println(
                    "Jugador conectado: " + name
            );

            JSONObject response = new JSONObject();

            response.put("type", "join_ok");
            response.put("message", "Bienvenido " + name);

            conn.send(response.toString());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("Error WebSocket: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Servidor WebSocket iniciado en el puerto " + getPort());
    }

    public static void main(String[] args) {

        Main server = new Main(
                new InetSocketAddress(DEFAULT_PORT)
        );

        server.start();

        System.out.println(
                "Servidor escuchando en el puerto " + DEFAULT_PORT
        );

        CountDownLatch latch = new CountDownLatch(1);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}