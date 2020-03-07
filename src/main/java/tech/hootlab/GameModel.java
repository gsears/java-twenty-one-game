package tech.hootlab;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

/*
 * Game.java Gareth Sears - 2493194S
 */

/**
 * A class representing the state of a game.
 */
public class GameModel implements PropertyChangeListener {

    public final int INITIAL_TOKENS = 200;
    public final int STAKE = 20;

    // Players
    Map<String, Player> playerMap = new HashMap<>();
    // Round
    Round round;
    // Dealer
    Player dealer;

    // Players added and removed from the game should be added on next round.
    public void addPlayer(String ID, String playerName) {
        Player player = new Player(ID, playerName, INITIAL_TOKENS);
        player.addPropertyChangeListener(this);
        playerMap.put(ID, player);
    }

    // This will be called by the client on close
    public void removePlayer(String ID) {
        Player player = playerMap.get(ID);
        removePlayer(player);
    }

    // This will be called by the server, on deadbeat-has-no-money-left.
    public void removePlayer(Player player) {
        if (dealer.equals(player) && playerMap.size() > 0) {
            // Assign dealer to first player in map
            dealer = playerMap.values().iterator().next();
        }

        if (round != null) {
            round.removePlayer(player);
        }

        playerMap.remove(player.getID());


        // Update clients so they can update their views?
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
}
