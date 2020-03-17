package tech.hootlab.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import tech.hootlab.Server;

public class Client {
    private final static Logger LOGGER = Logger.getLogger(Client.class.getName());

    private ClientController controller;
    private ClientView view;
    private Socket server;

    private Client() {

        try {
            server = new Socket("127.0.0.1", Server.SERVER_SOCKET);
            LOGGER.info("Connected to server: " + server);

            // TODO: Make this GUI rather than console
            // TODO: Perform error checking on input

            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter your name:");
            String name = scanner.nextLine();
            System.out.println("How many tokens?");
            int tokens = scanner.nextInt();
            scanner.close();

            // Create a settings object which is to be passed to the server.
            ClientSettings userSettings = new ClientSettings(name, tokens);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    controller = new ClientController(server, userSettings);
                    view = new ClientView(controller);
                    controller.setView(view);
                    view.setVisible(true);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}


