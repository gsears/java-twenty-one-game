package tech.hootlab;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server implements Runnable {
    public static final int SERVER_SOCKET = 1337;

    private ServerSocket server;
    private Map<String, SocketMessageSender> clientMap = new HashMap<>();

    // Game items
    private ServerController controller;

    public Server() {
        controller = new ServerController(clientMap, new ServerModel());
        connect();
    }

    private void connect() {
        try {
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
                ClientRunner client = new ClientRunner(clientSocket, controller);
                clientMap.put(client.getID(), client);

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
