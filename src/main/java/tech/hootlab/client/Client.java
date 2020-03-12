package tech.hootlab.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;
import tech.hootlab.Server;

public class Client {
    // assumes the current class is called MyLogger
    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());

    private ClientModel model;
    private ClientController controller;
    private ClientView view;

    private Socket server;

    private Client() {
        // Connect to server
        connect();

        // Get name on console.
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please enter your name:");
        String name = scanner.nextLine();
        LOGGER.info("Name chosen: " + name);

        // Doesn't error check at this stage...
        System.out.println("How many tokens you got, eh?");
        int tokens = scanner.nextInt();
        LOGGER.info("Tokens chosen: " + name);

        // Setup app
        model = new ClientModel();
        controller = new ClientController(model, server, new ClientSettings(name, tokens));
        view = new ClientView(model, controller);
        controller.setView(view);

        view.setVisible(true);

    }

    private void connect() {
        try {
            server = new Socket("127.0.0.1", Server.SERVER_SOCKET);
            LOGGER.info("Connected to server: " + server);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}
