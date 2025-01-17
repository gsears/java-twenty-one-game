package tech.hootlab.client;

import java.io.IOException;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import tech.hootlab.Server;

/*
 * Client.java
 *
 * Gareth Sears - 2493194S
 *
 * The main client class. Creates a connection with the server, prompts the user for their details,
 * then opens a swing window for their game session.
 */
public class Client {

    private static final String NAME_PROMPT_MESSAGE = "Please enter your name:";
    private static final String TOKEN_PROMPT_MESSAGE = "How many tokens have you got?";
    private static final String INVALID_TOKENS_MESSAGE = "Please enter a number greater than 0.";

    private ClientController controller;
    private ClientView view;
    private Socket server;

    // Singleton as this is executed in main
    private Client() {

        try {
            // Connect
            server = new Socket("127.0.0.1", Server.SERVER_SOCKET);

            // Prompt user for their details. Could be put in GUI, but swing is painful enough.
            Scanner scanner = new Scanner(System.in);
            System.out.println(NAME_PROMPT_MESSAGE);
            String name = scanner.nextLine();
            System.out.println(TOKEN_PROMPT_MESSAGE);

            // Error check input
            boolean incorrectInput = true;
            int tokens = 0;
            while (incorrectInput) {
                try {
                    tokens = scanner.nextInt();

                    if (tokens > 0) {
                        incorrectInput = false;
                    } else {
                        throw new InputMismatchException();
                    }

                } catch (InputMismatchException e) {
                    scanner.next();
                    System.out.println(INVALID_TOKENS_MESSAGE);
                }
            }

            scanner.close();

            // Create a settings object which is to be passed to the server by the controller.
            // Essentially this is a client 'model'.
            ClientSettings userSettings = new ClientSettings(name, tokens);

            // Setup the window, MVC style.
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


