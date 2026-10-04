package com.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main extends WebSocketServer { //.\run.ps1 com.server.Main

    public static final int DEFAULT_PORT = 3000;
    private final Map<WebSocket, Player> players = new ConcurrentHashMap<>();

    public Main(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Cliente conectado: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        Player player = players.remove(conn); //Eliminamos a los jugadores que se desconecten 

        if (player != null) {
            System.out.println(
                    "Jugador desconectado: " + player.getName()
            );
        }

    sendPlayers();
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        System.out.println("Mensaje recibido: " + message);

        JSONObject obj = new JSONObject(message);

        String type = obj.getString("type");

        if (type.equals("join")) {

            String name = obj.getString("name");

            Player player = new Player(name);

            players.put(conn, player);

            System.out.println("Jugador conectado: " + name);

            JSONObject response = new JSONObject();

            response.put("type", "join_ok");
            response.put("message", "Bienvenido " + name);

            conn.send(response.toString());

            sendPlayers();
            }
        }

    private void sendPlayers() { //Se encarga de convertir a los jugadores en JSON y enviarlos a todos los clientes 

    JSONArray playersArray = new JSONArray();

    for (Player player : players.values()) {

        JSONObject playerObj = new JSONObject();

        playerObj.put("name", player.getName());
        playerObj.put("score", player.getScore());

        playersArray.put(playerObj);
    }

    JSONObject response = new JSONObject();

    response.put("type", "players");
    response.put("players", playersArray);

    for (WebSocket conn : players.keySet()) {

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