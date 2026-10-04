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

    private final int[][] sudoku = {
    {5, 3, 4, 6, 7, 8, 9, 1, 2},
    {6, 7, 2, 1, 9, 5, 3, 4, 8},
    {1, 9, 8, 3, 4, 2, 5, 6, 7},
    {8, 5, 9, 7, 6, 1, 4, 2, 3},
    {4, 2, 6, 8, 5, 3, 7, 9, 1},
    {7, 1, 3, 9, 2, 4, 8, 5, 6},
    {9, 6, 1, 5, 3, 7, 2, 8, 4},
    {2, 8, 7, 4, 1, 9, 6, 3, 5},
    {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    private final boolean[][] casillasResueltas = crearCasillasResueltas();

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
            System.out.println("Jugador desconectado: " + player.getName());
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

            enviarEstadoTablero(conn);
            
        } else if (type.equals("guess")) {
            int fila = obj.getInt("fila");
            int columna = obj.getInt("columna");
            int numero = obj.getInt("numero");

            Player player = players.get(conn);

            if (casillasResueltas[fila][columna]) {
                return;
            }

            boolean correcto = sudoku[fila][columna] == numero;

            JSONObject response = new JSONObject();

            response.put("type", "guess_result");
            response.put("fila", fila);
            response.put("columna", columna);
            response.put("numero", numero);
            response.put("correcto", correcto);

            if (correcto) {

                casillasResueltas[fila][columna] = true;

                player.addScore(2);

            } else {

                player.addScore(-1);
            }

            if (correcto) {
                for (WebSocket jugador : players.keySet()) {
                    jugador.send(response.toString());
                }

            } else {
                conn.send(response.toString());
            }

            sendPlayers();

            if (correcto && partidaTerminada()) {

                JSONObject responseFinished = new JSONObject();

                responseFinished.put("type", "game_finished");

                for (WebSocket jugador : players.keySet()) {
                    jugador.send(responseFinished.toString());
                }
            }
        }
    }

    private void enviarEstadoTablero(WebSocket conn) {

        JSONArray casillas = new JSONArray();

        for (int fila = 0; fila < 9; fila++) {

            for (int columna = 0; columna < 9; columna++) {

                if (casillasResueltas[fila][columna]) {

                    JSONObject casilla = new JSONObject();

                    casilla.put("fila", fila);
                    casilla.put("columna", columna);
                    casilla.put("numero", sudoku[fila][columna]);

                    casillas.put(casilla);
                }
            }
        }

        JSONObject response = new JSONObject();

        response.put("type", "board_state");
        response.put("casillas", casillas);

        conn.send(response.toString());
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

    private boolean partidaTerminada() {

    for (int fila = 0; fila < 9; fila++) {

        for (int columna = 0; columna < 9; columna++) {

            if (!casillasResueltas[fila][columna]) {
                return false;
            }
        }
    }

    return true;
    }

    private boolean[][] crearCasillasResueltas() {

        boolean[][] resueltas = new boolean[9][9];

        int[][] tableroInicial = {
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

        for (int fila = 0; fila < 9; fila++) {

            for (int columna = 0; columna < 9; columna++) {

                if (tableroInicial[fila][columna] != 0) {
                    resueltas[fila][columna] = true;
                }
            }
        }

        return resueltas;
    }
}