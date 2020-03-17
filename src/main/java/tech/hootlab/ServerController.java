package tech.hootlab;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import tech.hootlab.client.ClientSettings;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

public class ServerController implements PropertyChangeListener {
    private final static Logger LOGGER = Logger.getLogger(ServerModel.class.getName());

    // For propagating messages to all clients
    Map<String, SocketMessageSender> clientMap;
    ServerModel model;
    Round round;

    public ServerController(Map<String, SocketMessageSender> clientMap, ServerModel model) {
        this.clientMap = clientMap;
        this.model = model;

        // Listen to round changes to propogate to clients
        round = model.getRound();
        round.addPropertyChangeListener(this);
    }

    public synchronized void addPlayer(String clientID, ClientSettings settings) {
        // Create player and listen for changes to propogate to clients
        Player player = new Player(clientID, settings.getName(), settings.getTokens());
        player.addPropertyChangeListener(this);

        // Tell other players a new player has entered.
        sendMessageToAll(SocketMessage.ADD_PLAYER, player);
        // Send the client their player object.
        sendMessage(clientID, SocketMessage.SET_USER, player);
        // Send the client a list of the current players so they can spectate round in
        // progress
        sendMessage(clientID, SocketMessage.SET_PLAYERS, (LinkedList<Player>) round.getPlayerList());

        model.addPlayer(player);

    }

    // This will be called by the client on close
    public synchronized void removePlayer(String clientID) {
        model.removePlayer(clientID);
        clientMap.remove(clientID);
        sendMessageToAll(SocketMessage.REMOVE_PLAYER, clientID);
    }

    public void hit(String ID) {
        round.hitWithCurrentPlayer();
    }

    public void stick(String ID) {
        round.stickWithCurrentPlayer();
    }

    public void deal(String ID) {
        LOGGER.info("Deal message received");
        round.start();
    }

    // TODO: Deal with property changes in a more systematic fashion

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {

            // Player Events
            case Player.HAND_CHANGE_EVENT:
                sendMessageToAll(SocketMessage.HAND_UPDATE, (Player) evt.getSource());
                break;

            case Player.STATUS_CHANGE_EVENT:
                LOGGER.info("Sending STATUS_CHANGE_EVENT");
                sendMessageToAll(SocketMessage.STATUS_UPDATE, (Player) evt.getSource());
                break;

            case Player.TOKEN_CHANGE_EVENT:
                sendMessageToAll(SocketMessage.TOKEN_UPDATE, (Player) evt.getSource());
                break;

            case Round.CURRENT_PLAYER_CHANGE_EVENT:
                sendMessageToAll(SocketMessage.ROUND_PLAYER_CHANGE, (Player) evt.getNewValue());
                break;

            case Round.DEALER_CHANGE_EVENT:
                Player newDealer = (Player) evt.getNewValue();
                model.setDealer(newDealer);
                sendMessageToAll(SocketMessage.PLAYER_CHANGE, newDealer);
                break;

            case Round.STATE_CHANGE_EVENT:
                handleRoundChange((RoundState) evt.getNewValue());
                break;

            default:
                break;
        }
    }

    private void handleRoundChange(RoundState roundState) {
        LOGGER.info("Received roundState event: " + roundState);
        switch (roundState) {
            case READY:
                sendMessageToAll(SocketMessage.SET_PLAYERS, (LinkedList<Player>) round.getPlayerList());
                sendMessageToAll(SocketMessage.ROUND_STARTED, model.getDealer());
                break;

            case IN_PROGRESS:
                sendMessageToAll(SocketMessage.ROUND_IN_PROGRESS, null);
                break;

            case FINISHED:
                sendMessageToAll(SocketMessage.ROUND_FINISHED, null);
                // Kick out any dead-beat no has-moneys.
                List<Player> deadbeatList = model.removeBrokePlayers();
                deadbeatList.forEach(p -> sendMessage(p.getID(), SocketMessage.DISCONNECT, null));
                if (model.getPlayerList().size() > 1) {
                    model.startNextRound();
                }
                break;

            default:
                break;
        }
    }

    private void sendMessageToAll(String message, Serializable payload) {
        sendMessageToAll(new SocketMessage(message, payload));
    }

    private void sendMessageToAll(SocketMessage messageObject) {
        clientMap.values().forEach(client -> {
            client.sendMessage(messageObject);
        });
    }

    private void sendMessage(String ID, String message, Serializable payload) {
        sendMessage(ID, new SocketMessage(message, payload));
    }

    private void sendMessage(String ID, SocketMessage messageObject) {
        clientMap.get(ID).sendMessage(messageObject);
    }

}
