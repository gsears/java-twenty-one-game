package tech.hootlab.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.SwingWorker;
import tech.hootlab.SocketMessage;
import tech.hootlab.core.Player;

/*
 * ClientController.java
 *
 * Gareth Sears - 2493194S
 *
 * This class handles inputs / outputs to the view class, including managing the socket API.
 *
 */
public class ClientController {

    // Display Messages
    // Could have been created in view, but this was a design decision for a more agnostic view.
    private final static String SERVER_DISCONNECT_MESSAGE =
            "Server has been disconnected. Thanks for playing.";
    private final static String USER_CONNECTED_MESSAGE =
            "Waiting for next round to join... (Spectating)";
    private final static String ROUND_INITIALISED_MESSAGE =
            "New Round Started. Waiting for dealer to deal...";
    private final static String ROUND_IN_PROGRESS_MESSAGE =
            "Round in progress... Aim for that 21 buddy!";
    private final static String ROUND_FINISHED_MESSAGE =
            "Round finished! Waiting for players for next round...";
    private final static String NO_TOKEN_DISCONNECT_MESSAGE =
            "No dead-beat-no-has-moneys allowed here.\nCome back when you've got more tokens!";

    private final Socket server;
    private final ClientSettings clientSettings;

    private String userID;
    private ClientView view;

    private WriteWorker writeWorker;

    public ClientController(Socket server, ClientSettings clientSettings) {
        this.clientSettings = clientSettings;
        this.server = server;
    }

    /**
     * Sets the view controlled by this controller.
     *
     * @param view The view object.
     */
    public void setView(ClientView view) {
        this.view = view;

        // Initialise IO when a view is ready to handle messages
        writeWorker = new WriteWorker(server);
        writeWorker.execute();

        ReadWorker rw = new ReadWorker(server);
        rw.execute();
    }

    /**
     * Private class to handle incoming server messages
     */
    private class WriteWorker extends SwingWorker<Void, SocketMessage> {

        private ObjectOutputStream objectOutputStream;
        private BlockingQueue<SocketMessage> messageQueue = new LinkedBlockingQueue<>();

        public WriteWorker(Socket server) {
            try {
                objectOutputStream = new ObjectOutputStream(server.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * As the write messages may come outside of this worker thread, it pushes them to a
         * blocking queue to be processed. This thread is the 'consumer', the other thread is the
         * the 'producer'.
         *
         * @param message The socket message. SocketMessage.POISON is the poison pill.
         */
        public void write(SocketMessage message) {
            try {
                messageQueue.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            boolean run = true;
            while (run) {
                SocketMessage message = messageQueue.take();
                String messageCommand = message.getCommand();
                if (messageCommand.equals(SocketMessage.POISON)) {
                    // Shut down worker on poison pill
                    run = false;
                } else {
                    objectOutputStream.writeObject(message);
                }
            }

            // The server is closed in the ReadWorker thread. No need to close here.
            return null;
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

        /**
         * Collects and publishes API messages from the server to be processed on the Swing thread.
         */
        public Void doInBackground() {

            boolean run = true;
            try {
                while (run) {
                    SocketMessage message = (SocketMessage) inputStream.readObject();
                    if (message.getCommand().equals(SocketMessage.DISCONNECT)) {
                        run = false;
                    }
                    publish(message);
                }
            } catch (EOFException e) {
                disconnect(SERVER_DISCONNECT_MESSAGE);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Handles messages on the Swing thread.
         */
        protected void process(List<SocketMessage> messageList) {
            for (SocketMessage message : messageList) {
                handleServerMessage(message);
            }
        }

        /**
         * This class redirects the API messages to their respective functions, ensuring that the
         * message payloads are cast appropriately.
         *
         * @param message The API message
         */
        @SuppressWarnings("unchecked")
        protected void handleServerMessage(SocketMessage message) {
            String command = message.getCommand();
            switch (command) {

                // Connection Actions
                case SocketMessage.CONNECT:
                    connect((String) message.getPayload());
                    break;

                case SocketMessage.DISCONNECT:
                    disconnect(NO_TOKEN_DISCONNECT_MESSAGE);
                    break;

                // Global Actions
                case SocketMessage.SET_USER:
                    setUser((Player) message.getPayload());
                    break;

                case SocketMessage.SET_PLAYERS:
                    updateOtherPlayers((List<Player>) message.getPayload());
                    break;

                // Round Actions
                case SocketMessage.ROUND_PLAYER_CHANGE:
                    updateCurrentPlayer((Player) message.getPayload());
                    break;

                case SocketMessage.ROUND_STARTED:
                    roundStarted((Player) message.getPayload());
                    break;

                case SocketMessage.ROUND_IN_PROGRESS:
                    roundInProgress();
                    break;

                case SocketMessage.ROUND_FINISHED:
                    roundFinished();
                    break;

                // Player Actions
                case SocketMessage.HAND_UPDATE:
                    updateHand((Player) message.getPayload());
                    break;

                case SocketMessage.TOKEN_UPDATE:
                    updatePlayerTokens((Player) message.getPayload());
                    break;

                case SocketMessage.STATUS_UPDATE:
                    updatePlayerStatus((Player) message.getPayload());
                    break;

                default:
                    throw new IllegalArgumentException("Unknown message received");
            }
        }
    }

    // Connection Handlers

    private void connect(String clientID) {
        this.userID = clientID;
        sendMessage(SocketMessage.CONNECT, clientSettings);
    }

    /**
     * Disconnects the client.
     *
     * @param disconnectMessage The message stating the reason for disconnecting.
     */
    public void disconnect(String disconnectMessage) {
        System.out.println(disconnectMessage);
        quit();
    }

    private void quit() {
        System.exit(0);
    }

    // Global Handlers

    private void setUser(Player userPlayer) {
        view.setUser(userPlayer);
        view.displayMessage(USER_CONNECTED_MESSAGE);
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

        if (currentPlayer != null && currentPlayer.getID().equals(userID)) {
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

        view.displayMessage(ROUND_INITIALISED_MESSAGE);
    }

    private void roundInProgress() {
        view.displayMessage(ROUND_IN_PROGRESS_MESSAGE);
        // This will be set when player is current_player again
        view.disableControl();
    }

    private void roundFinished() {
        view.displayMessage(ROUND_FINISHED_MESSAGE);
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
        view.updateStatus(player);
    }

    // Messages to server
    public void hit() {
        sendMessage(SocketMessage.HIT);
    }

    public void stick() {
        sendMessage(SocketMessage.STICK);
    }

    public void deal() {
        sendMessage(SocketMessage.DEAL);
    }

    // Helper methods for sending messages to writeWorker
    private void sendMessage(String message) {
        sendMessage(message, null);
    }

    private void sendMessage(String message, Serializable payload) {
        writeWorker.write(new SocketMessage(message, payload));
    }
}
