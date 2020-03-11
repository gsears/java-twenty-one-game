package tech.hootlab;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.logging.Logger;
import tech.hootlab.client.ClientSettings;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;
import tech.hootlab.server.ClientServerMessage;

/*
 * Game.java Gareth Sears - 2493194S
 */

/**
 * A class representing the state of a game.
 */
public class GameController implements PropertyChangeListener {
    private final static Logger LOGGER = Logger.getLogger(GameController.class.getName());

    Set<ClientRunner> clientSet;
    GameModel model;

    public GameController(Set<ClientRunner> clientSet, GameModel model) {
        this.clientSet = clientSet;
        this.model = model;
    }

    // Players added and removed from the game should be added on next round.
    public synchronized void addPlayer(String clientID, ClientSettings settings) {
        Player player = model.addPlayer(clientID, settings.getName());
        player.setTokens(settings.getTokens());
        player.addPropertyChangeListener(this);
        LOGGER.info("Player added to model.");
        // Tell clients we've got a new player
        sendMessage(new ClientServerMessage(ClientServerMessage.ADD_PLAYER, player));
    }

    // This will be called by the client on close
    public synchronized void removePlayer(String ID) {
        Player player = model.removePlayer(ID);
        LOGGER.info("Player removed from model.");
        sendMessage(new ClientServerMessage(ClientServerMessage.REMOVE_PLAYER, player));
    }

    public void hit(String ID) {
        LOGGER.info("Hit()");
    }

    public void stick(String ID) {
        LOGGER.info("Stick()");
    }

    public void deal(String ID) {
        LOGGER.info("Deal()");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {

            // Player Events
            case Player.HAND_CHANGE_EVENT:
                // Serialise and send
                break;

            case Player.STATUS_CHANGE_EVENT:
                // Serialise and send
                break;

            case Player.TOKEN_CHANGE_EVENT:
                // Serialise and send
                break;

            case Round.CURRENT_PLAYER_CHANGE_EVENT:
                // Serialise and send
                break;

            case Round.STATE_CHANGE_EVENT:
                // Controller
                handleRoundChange((RoundState) evt.getNewValue());
                // Serialise and send to clients

                break;

            default:
                break;
        }
    }

    private void handleRoundChange(RoundState roundState) {
        switch (roundState) {
            case READY:
                // Populate client views in player order
                // Notify dealer to deal
                break;

            case IN_PROGRESS:
                // Doesn't do much...I don't think...
                break;

            case FINISHED:
                // TODO: Need to pause here somehow...
                // round.getDealer();
                break;

            default:
                break;
        }
    }

    private void sendMessage(ClientServerMessage messageObject) {
        clientSet.forEach(client -> {
            client.sendMessage(messageObject);
        });
    }

}
