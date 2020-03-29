package tech.hootlab;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    public static final int SERVER_SOCKET = 1337;

    private ServerSocket server;
    private ServerController controller;

    public Server() {
        controller = new ServerController();
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
                controller.addClient(new ClientRunner(clientSocket, controller));
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
