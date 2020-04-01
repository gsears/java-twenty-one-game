package tech.hootlab;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import tech.hootlab.client.ClientSettings;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

/*
 * ServerController.java
 *
 * Gareth Sears - 2493194S
 *
 * This class is responsible for routing messages between the clients and the model. As the model is
 * designed to be thread-safe, little synchronisation is done here, but it does use a
 * ConcurrentHashMap to keep track of clients.
 *
 * It manages most of its client interactions by listening to the model.
 */
public class ServerController {

    // This is hard coded, but could be modified in later versions.
    public final int ROUND_STAKE = 20;

    // For propagating messages to all clients
    private Map<String, ClientRunner> clientMap = new ConcurrentHashMap<>();
    private ServerModel model;

    public ServerController() {
        // Create the model locally to avoid to much shared state in multithread environment.
        this.model = new ServerModel(ROUND_STAKE);

        // Attach listeners to the model's round object.
        // Uses anonymous lambda functions for brevity.
        model.addRoundPropertyChangeListener(Round.CURRENT_PLAYER_CHANGE_EVENT, evt -> {
            sendMessageToAll(SocketMessage.ROUND_PLAYER_CHANGE, (Player) evt.getNewValue());
        });

        model.addRoundPropertyChangeListener(Round.DEALER_CHANGE_EVENT, evt -> {
            Player newDealer = (Player) evt.getNewValue();
            model.setDealer(newDealer);
        });

        model.addRoundPropertyChangeListener(Round.STATE_CHANGE_EVENT, evt -> {
            RoundState roundState = (RoundState) evt.getNewValue();
            switch (roundState) {
                case READY:
                    sendMessageToAll(SocketMessage.SET_PLAYERS,
                            // Cast to Serializable, as we do not know the type of List from
                            // Collections.unmodifiableList, but it is guaranteed to be Serializable
                            // as per docs.
                            (Serializable) model.getPlayersInRound());
                    sendMessageToAll(SocketMessage.ROUND_STARTED, model.getDealer());
                    break;

                case IN_PROGRESS:
                    sendMessageToAll(SocketMessage.ROUND_IN_PROGRESS, null);
                    break;

                case FINISHED:
                    sendMessageToAll(SocketMessage.ROUND_FINISHED, null);
                    // Kick out any dead-beat no has-moneys.
                    List<Player> brokeList = model.removeBrokePlayers();
                    // This will disconnect them clientside, which will in turn remove them return
                    // an end of file connection in ClientRunner which will initialize standard
                    // removal process.
                    brokeList.forEach(p -> sendMessage(p.getID(), SocketMessage.DISCONNECT, null));

                    // Start new round
                    model.initialiseNextRound();
                    break;

                default:
                    throw new IllegalStateException(roundState + " is not a state that is handled");
            }
        });
    }

    public void addClient(ClientRunner client) {
        final String clientID = client.getID();
        clientMap.put(clientID, client);
        // Send the client their ID on connection
        sendMessage(clientID, new SocketMessage(SocketMessage.CONNECT, clientID));
    }

    public void addPlayer(String clientID, ClientSettings settings) {
        // No need to synchronise this, as it's all setup code which only pertains to the
        // caller... no shared state outside of this method.

        // Create player and listen for changes to propogate to clients
        Player player = new Player(clientID, settings.getName(), settings.getTokens());

        // Attach listeners to the player object
        // These generally indicate changes to clients
        player.addPropertyChangeListener(Player.HAND_CHANGE_EVENT, evt -> {
            sendMessageToAll(SocketMessage.HAND_UPDATE, (Player) evt.getSource());
        });

        player.addPropertyChangeListener(Player.TOKEN_CHANGE_EVENT, evt -> {
            sendMessageToAll(SocketMessage.TOKEN_UPDATE, (Player) evt.getSource());
        });

        player.addPropertyChangeListener(Player.STATUS_CHANGE_EVENT, evt -> {
            sendMessageToAll(SocketMessage.STATUS_UPDATE, (Player) evt.getSource());
        });

        // Send the client their player object.
        sendMessage(clientID, SocketMessage.SET_USER, player);
        // Send the client a list of the current players so they can spectate round in
        // progress
        sendMessage(clientID, SocketMessage.SET_PLAYERS, (Serializable) model.getPlayersInRound());

        // This is internally thread-safe. Delays getting here should be tolerable as in the
        // totally unlikely worst case scenario, the player will miss a round or two, but the
        // game's logic will be sound. Like all concurrent things... we'll 'eventually' be good.
        model.addPlayer(player);
    }

    public void removePlayer(String clientID) {
        // Remove client from map so they are not updated with subsequent messages
        clientMap.remove(clientID);
        // This is internally thread-safe
        model.removePlayer(clientID);

    }

    public void hit(String ID) {
        model.hitWithCurrentPlayer();
    }

    public void stick(String ID) {
        model.stickWithCurrentPlayer();
    }

    public void deal(String ID) {
        model.startRound();
    }

    private void sendMessageToAll(String message, Serializable payload) {
        sendMessageToAll(new SocketMessage(message, payload));
    }

    private void sendMessageToAll(SocketMessage messageObject) {
        // In spec: Iterators are designed to be only used by one thread at a time
        // so we lock the map here.
        synchronized (clientMap) {
            clientMap.values().forEach(client -> {
                client.sendMessage(messageObject);
            });
        }
    }

    private void sendMessage(String ID, String message, Serializable payload) {
        sendMessage(ID, new SocketMessage(message, payload));
    }

    private void sendMessage(String ID, SocketMessage messageObject) {
        ClientRunner client = clientMap.get(ID);
        if (client != null) {
            client.sendMessage(messageObject);
        }
    }
}
