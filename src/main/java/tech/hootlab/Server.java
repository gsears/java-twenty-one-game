package tech.hootlab;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Server implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static final int SERVER_SOCKET = 8765;

    private ServerSocket server;
    private Map<String, SocketMessageSender> clientMap = new HashMap<>();

    // Game items
    private ServerController controller;

    public Server() {
        LOGGER.info("Initialising new server");
        controller = new ServerController(clientMap, new ServerModel());
        connect();
    }

    private void connect() {
        try {
            LOGGER.info("Creating server socket");
            server = new ServerSocket(SERVER_SOCKET);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = server.accept();
                LOGGER.info("Client connected.");

                ClientRunner client = new ClientRunner(clientSocket, controller);
                LOGGER.info("ClientRunner created with ID: " + client);

                clientMap.put(client.getID(), client);
                LOGGER.info("Current client set: " + clientMap.keySet());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        Thread t = new Thread(new Server());
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
