package tech.hootlab.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import tech.hootlab.SocketMessage;
import tech.hootlab.core.Hand;
import tech.hootlab.core.Player;

public class ClientController {
    private final static Logger LOGGER = Logger.getLogger(ClientController.class.getName());

    private String userID;
    private ClientSettings clientSettings;
    private ClientView view;
    private Socket server;

    private ObjectOutputStream objectOutputStream;

    public ClientController(Socket server, ClientSettings clientSettings) {

        this.clientSettings = clientSettings;
        this.server = server;

        // For writing messages from action events (short enough to not need a thread)
        try {
            objectOutputStream = new ObjectOutputStream(server.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            } catch (EOFException e) {
                LOGGER.warning("SERVER DISCONNECTED");
                quit();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void process(List<SocketMessage> messageList) {
            LOGGER.info("Processing message: " + messageList);
            // process all messages
            for (SocketMessage message : messageList) {
                switch (message.getMessage()) {
                    case SocketMessage.CONNECT:
                        LOGGER.info("Received CONNECT message");
                        // Set the user player
                        connect((String) message.getContent());
                        break;

                    case SocketMessage.SET_USER:
                        LOGGER.info("Received SET_USER message");
                        setUser((Player) message.getContent());
                        break;

                    case SocketMessage.NEW_ROUND:
                        LOGGER.info("Received NEW_ROUND message");
                        setDealer((Player) message.getContent());

                        break;

                    case SocketMessage.SET_PLAYERS:
                        LOGGER.info("Received SET_PLAYERS message");
                        updatePlayers((List<Player>) message.getContent());
                        break;

                    case SocketMessage.HAND_UPDATE:
                        LOGGER.info("Received HAND_UPDATE message");
                        updateHand((Player) message.getContent());
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private void connect(String clientID) {
        this.userID = clientID;
        LOGGER.info("Sending settings to server...");
        sendMessage(SocketMessage.CONNECT, clientSettings);
    }

    private void setUser(Player userPlayer) {
        view.setUser(userPlayer);
        view.displayMessage("Waiting for next round to begin...");
    }

    private void updatePlayers(List<Player> playerList) {
        view.clearPlayers();
        for (Player player : playerList) {
            addPlayer(player);
        }
    }

    private void updateHand(Player player) {
        LOGGER.info("Player's hand to update: " + player.getID());
        LOGGER.info("Hand value: " + player.getHandValue());
        LOGGER.info("Update hand with: " + player.getHand().getCardList());
        view.updateHand(player);
    }

    private void addPlayer(Player player) {
        if (!userID.equals(player.getID())) {
            view.addPlayer(player);
        }
    }

    public void quit() {
        System.exit(0);
    }

    // Messages from server
    public void updateHand(Hand hand) {

    }


    public void setDealer(Player player) {
        view.setDealer(player);

        if (player.getID().equals(userID)) {
            view.setDealerControl();
        } else {
            view.disableControl();
        }

        view.displayMessage("NEW ROUND: Waiting for dealer to DEAL!");
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
