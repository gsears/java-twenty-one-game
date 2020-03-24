package tech.hootlab;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tech.hootlab.client.ClientSettings;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

public class ServerController {

    // For propagating messages to all clients
    Map<String, SocketMessageSender> clientMap;
    ServerModel model;
    Round roundModel;

    public ServerController(Map<String, SocketMessageSender> clientMap, ServerModel model) {
        this.clientMap = clientMap;
        this.model = model;

        // Attach listeners to the model's round object
        roundModel = model.getRound();

        roundModel.addPropertyChangeListener(Round.CURRENT_PLAYER_CHANGE_EVENT, evt -> {
            sendMessageToAll(SocketMessage.ROUND_PLAYER_CHANGE, (Player) evt.getNewValue());
        });

        roundModel.addPropertyChangeListener(Round.DEALER_CHANGE_EVENT, evt -> {
            Player newDealer = (Player) evt.getNewValue();
            model.setDealer(newDealer);
        });

        roundModel.addPropertyChangeListener(Round.STATE_CHANGE_EVENT, evt -> {
            RoundState roundState = (RoundState) evt.getNewValue();
            switch (roundState) {
                case READY:
                    sendMessageToAll(SocketMessage.SET_PLAYERS,
                            (LinkedList<Player>) roundModel.getPlayerList());
                    sendMessageToAll(SocketMessage.ROUND_STARTED, model.getDealer());
                    break;

                case IN_PROGRESS:
                    sendMessageToAll(SocketMessage.ROUND_IN_PROGRESS, null);
                    break;

                case FINISHED:
                    sendMessageToAll(SocketMessage.ROUND_FINISHED, null);
                    // Kick out any dead-beat no has-moneys.
                    List<Player> brokeList = model.removeBrokePlayers();
                    brokeList.forEach(p -> sendMessage(p.getID(), SocketMessage.DISCONNECT, null));
                    if (model.getPlayerList().size() > 1) {
                        model.startNextRound();
                    }
                    break;

                default:
                    throw new IllegalStateException(roundState + " is not a state that is handled");
            }
        });
    }

    public void addPlayer(String clientID, ClientSettings settings) {
        // Create player and listen for changes to propogate to clients
        Player player = new Player(clientID, settings.getName(), settings.getTokens());

        // Attach listeners to the player object
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
        sendMessage(clientID, SocketMessage.SET_PLAYERS,
                (LinkedList<Player>) roundModel.getPlayerList());

        model.addPlayer(player);

    }

    public synchronized void removePlayer(String clientID) {
        model.removePlayer(clientID);
        clientMap.remove(clientID);
    }

    public void hit(String ID) {
        roundModel.hitWithCurrentPlayer();
    }

    public void stick(String ID) {
        roundModel.stickWithCurrentPlayer();
    }

    public void deal(String ID) {
        roundModel.start();
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
