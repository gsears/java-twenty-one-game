package tech.hootlab.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import tech.hootlab.SocketMessage;
import tech.hootlab.core.Player;

public class ClientController {
    private final static Logger LOGGER = Logger.getLogger(ClientController.class.getName());

    private String userID;
    private ClientSettings clientSettings;
    private ClientView view;
    private Socket server;

    private WriteWorker writeWorker;

    public ClientController(Socket server, ClientSettings clientSettings) {
        this.clientSettings = clientSettings;
        this.server = server;
    }

    public void setView(ClientView view) {
        LOGGER.info("View set to: " + view);
        this.view = view;

        writeWorker = new WriteWorker(server);
        writeWorker.execute();

        ReadWorker rw = new ReadWorker(server);
        rw.execute();

    }

    private class WriteWorker extends SwingWorker<Void, SocketMessage> {

        private ObjectOutputStream objectOutputStream;
        private ConcurrentLinkedQueue<SocketMessage> messageQueue = new ConcurrentLinkedQueue<>();

        public WriteWorker(Socket server) {
            try {
                objectOutputStream = new ObjectOutputStream(server.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void write(SocketMessage message) {
            messageQueue.add(message);
        }

        @Override
        protected Void doInBackground() throws Exception {
            while (true) {
                if (!messageQueue.isEmpty()) {
                    LOGGER.info(messageQueue.element().getCommand());
                    objectOutputStream.writeObject(messageQueue.poll());
                }
            }
        }
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
                handleServerMessage(message);
            }
        }

        protected void handleServerMessage(SocketMessage message) {
            String command = message.getCommand();
            switch (command) {

                // Connection Actions

                case SocketMessage.CONNECT:
                    LOGGER.info("Received CONNECT message");
                    connect((String) message.getPayload());
                    break;

                case SocketMessage.DISCONNECT:
                    LOGGER.info("Received DISCONNECT message");
                    disconnect();
                    break;

                // Global Actions

                case SocketMessage.SET_USER:
                    LOGGER.info("Received SET_USER message");
                    setUser((Player) message.getPayload());
                    break;

                case SocketMessage.SET_PLAYERS:
                    LOGGER.info("Received SET_PLAYERS message");
                    updateOtherPlayers((List<Player>) message.getPayload());
                    break;

                // Round Actions

                case SocketMessage.ROUND_PLAYER_CHANGE:
                    updateCurrentPlayer((Player) message.getPayload());
                    break;

                case SocketMessage.ROUND_STARTED:
                    LOGGER.info("Received NEW_ROUND message");
                    roundStarted((Player) message.getPayload());
                    break;

                case SocketMessage.ROUND_IN_PROGRESS:
                    LOGGER.info("Received ROUND_IN_PROGRESS message");
                    roundInProgress();
                    break;

                case SocketMessage.ROUND_FINISHED:
                    LOGGER.info("Received ROUND_FINISHED message");
                    roundFinished();
                    break;

                // Player Actions

                case SocketMessage.HAND_UPDATE:
                    LOGGER.info("Received HAND_UPDATE message");
                    updateHand((Player) message.getPayload());
                    break;

                case SocketMessage.TOKEN_UPDATE:
                    LOGGER.info("Received TOKEN_UPDATE message");
                    updatePlayerTokens((Player) message.getPayload());
                    break;

                case SocketMessage.STATUS_UPDATE:
                    LOGGER.info("Received STATUS_UPDATE message");
                    updatePlayerStatus((Player) message.getPayload());
                    break;

                default:
                    LOGGER.info("Invalid message received!");
                    LOGGER.info(message.getCommand() + ": " + message.getPayload());
                    break;
            }
        }
    }

    // Connection Handlers

    private void connect(String clientID) {
        this.userID = clientID;
        LOGGER.info("Sending settings to server...");
        sendMessage(SocketMessage.CONNECT, clientSettings);
    }

    // Global Handlers

    private void setUser(Player userPlayer) {
        view.setUser(userPlayer);
        view.displayMessage("Waiting for next round to join...");
    }

    private void updateOtherPlayers(List<Player> playerList) {
        view.clearPlayers();
        for (Player player : playerList) {
            if (!userID.equals(player.getID())) {
                view.addPlayer(player);
            }
        }
    }

    // Round Handlers

    private void updateCurrentPlayer(Player currentPlayer) {
        view.setCurrentPlayer(currentPlayer);

        if (currentPlayer.getID().equals(userID)) {
            view.setPlayerControl();
        } else {
            view.disableControl();
        }
    }

    private void roundStarted(Player dealer) {
        view.setDealer(dealer);

        if (dealer.getID().equals(userID)) {
            view.setDealerControl();
        } else {
            view.disableControl();
        }

        view.displayMessage("New Round Started. Waiting for dealer...");
    }

    private void roundInProgress() {
        view.displayMessage("Round in progress...");
        // This will be set when player is current_player again
        view.disableControl();
    }

    private void roundFinished() {
        view.displayMessage("Round finished!");
        view.disableControl();
    }

    // Player handlers

    private void updateHand(Player player) {
        view.updateHand(player);
    }

    private void updatePlayerTokens(Player player) {
        view.updateTokens(player);
    }

    private void updatePlayerStatus(Player player) {
        LOGGER.info("Updating Player Status");
        view.updateStatus(player);
    }

    public void quit() {
        System.exit(0);
    }

    // Messages to server

    public void disconnect() {
        sendMessage(SocketMessage.DISCONNECT);
        System.out.println("COME BACK WHEN YEE GIT MORE TOKENS!");
        quit();
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
        writeWorker.write(new SocketMessage(message, payload));
    }
}
