package tech.hootlab;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import tech.hootlab.client.ClientSettings;
import tech.hootlab.server.SocketMessage;

public class ClientRunner {
    // assumes the current class is called MyLogger
    private final static Logger LOGGER = Logger.getLogger(ClientRunner.class.getName());

    private Socket client;
    private String clientID;

    private ClientReader clientReader;
    private ClientWriter clientWriter;

    private ServerController controller;

    private boolean isConnected = true;

    public ClientRunner(Socket client, ServerController controller) {
        this.controller = controller;

        this.clientID = UUID.randomUUID().toString();
        this.client = client;

        clientReader = new ClientReader();
        Thread readThread = new Thread(clientReader);
        readThread.start();

        clientWriter = new ClientWriter();
        Thread writeThread = new Thread(clientWriter);
        writeThread.start();

        // Tell the player their ID so they can differentiate themselves from other players they
        // receive. #1 - This is the first stage in the 'connection'
        sendMessage(new SocketMessage(SocketMessage.CONNECT, clientID));

    }

    public String getID() {
        return clientID;
    }

    public void sendMessage(SocketMessage message) {
        clientWriter.sendMessage(message);
    }

    public void disconnect() {
        LOGGER.info("disconnect()");
        isConnected = false;
    }

    // Private class so we can access instance variables
    private class ClientReader implements Runnable {

        ObjectInputStream objectInputStream;

        public ClientReader() {
            try {
                objectInputStream = new ObjectInputStream(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (isConnected) {
                try {
                    SocketMessage message = null;

                    while ((message = (SocketMessage) objectInputStream.readObject()) != null) {

                        switch (message.getMessage()) {
                            case SocketMessage.CONNECT:
                                // API CONTENT:
                                // (ClientSettings) settings for the player (tokens and name)
                                LOGGER.info("SET message received!");

                                controller.addPlayer(clientID,
                                        (ClientSettings) message.getContent());

                                break;

                            case SocketMessage.HIT:
                                // API CONTENT:
                                // (String) playerName
                                controller.hit((String) clientID);
                                break;

                            case SocketMessage.STICK:
                                // API CONTENT:
                                // (String) playerName
                                controller.stick((String) clientID);
                                break;

                            case SocketMessage.DEAL:
                                // API CONTENT:
                                // (String) playerName
                                controller.deal((String) clientID);
                                break;

                            default:
                                break;
                        }
                    }

                    objectInputStream.close();

                } catch (EOFException e) {
                    // Disconnect the player
                    LOGGER.warning(clientID + " has disconnected");
                    disconnect();
                    controller.removePlayer(clientID);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    // Private class so we can access instance variables
    private class ClientWriter implements Runnable {

        ConcurrentLinkedQueue<SocketMessage> messageQueue = new ConcurrentLinkedQueue<>();
        ObjectOutputStream objectOutputStream;

        public ClientWriter() {
            try {
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(SocketMessage message) {
            messageQueue.add(message);
        }

        @Override
        public void run() {
            while (isConnected) {
                try {
                    if (!messageQueue.isEmpty()) {
                        objectOutputStream.writeObject(messageQueue.poll());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    @Override
    public String toString() {
        return clientID;
    }

}
