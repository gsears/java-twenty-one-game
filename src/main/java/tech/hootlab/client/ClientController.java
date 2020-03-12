package tech.hootlab.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import tech.hootlab.core.Hand;
import tech.hootlab.core.Player;
import tech.hootlab.server.SocketMessage;

public class ClientController {
    private final static Logger LOGGER = Logger.getLogger(ClientController.class.getName());

    private String userID;
    private ClientSettings clientSettings;
    private ClientModel model;
    private ClientView view;

    private ObjectOutputStream objectOutputStream;

    public ClientController(ClientModel model, Socket server, ClientSettings clientSettings) {
        this.model = model;
        this.clientSettings = clientSettings;

        // For writing messages from action events (short enough to not need a thread)
        try {
            objectOutputStream = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }



        // Connect with user settings

    }

    public void setView(ClientView view) {
        LOGGER.info("View set to: " + view);
        this.view = view;

        ReadWorker rw = new ReadWorker(server);
        rw.execute();
    }

    private class ReadWorker extends SwingWorker<Void, SocketMessage> {
        private ObjectInputStream inputStream;

        public ReadWorker(Socket server) {
            try {
                inputStream = new ObjectInputStream(server.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Void doInBackground() {
            SocketMessage message;
            try {
                while ((message = (SocketMessage) inputStream.readObject()) != null) {
                    publish(message);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void process(List<SocketMessage> messageList) {
            LOGGER.info("Processing message: " + messageList);
            // Pop last message
            SocketMessage message = messageList.get(messageList.size() - 1);

            switch (message.getMessage()) {
                case SocketMessage.CONNECT:
                    // #2 - Second stage of connection, set the ID here
                    connect((String) message.getContent());
                    break;

                case SocketMessage.ADD_PLAYER:
                    addPlayer((Player) message.getContent());
                    break;

                case SocketMessage.REMOVE_PLAYER:
                    removePlayer((Player) message.getContent());
                    break;

                default:
                    break;
            }
        }
    }

    private synchronized void connect(String userID) {
        this.userID = userID;
        LOGGER.info("Sending settings to server...");
        sendMessage(SocketMessage.CONNECT, clientSettings);
    }

    private void addPlayer(Player player) {
        if (player.getID().equals(userID)) {
            view.setUser(player);
        } else {
            view.addPlayer(player);
        }
    }

    private synchronized void removePlayer(Player player) {
        if (player.getID().equals(userID)) {
            // You've been kicked out for being too poor, you deadbeat.
            quit();
        } else {
            view.removePlayer(player);
        }
    }

    public void quit() {
        System.exit(0);
    }

    // Messages from server
    public void updateHand(Hand hand) {

    }


    public void setDealer(Player player) {
        model.setDealer(player);
    }

    public void setCurrentPlayer(Player player) {
        model.setCurrentPlayer(player);
    }

    // Messages to server
    public void disconnect() {
        sendMessage(SocketMessage.DISCONNECT);
        LOGGER.info("disconnect");
    }

    public void hit() {
        sendMessage(SocketMessage.HIT);
        LOGGER.info("hit");
    }

    public void stick() {
        sendMessage(SocketMessage.STICK);
        LOGGER.info("stick");
    }

    public void deal() {
        sendMessage(SocketMessage.DEAL);
        LOGGER.info("deal");
    }

    private void sendMessage(String message) {
        sendMessage(message, null);
    }

    private void sendMessage(String message, Serializable payload) {
        try {
            objectOutputStream.writeObject(new SocketMessage(message, payload));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
