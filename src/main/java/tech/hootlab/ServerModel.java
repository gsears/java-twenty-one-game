package tech.hootlab;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

/*
 * Game.java Gareth Sears - 2493194S
 */

/**
 * A class representing the state of a game.
 */
public class ServerModel {
    private final static Logger LOGGER = Logger.getLogger(ServerModel.class.getName());

    public final int STAKE = 20;

    Map<String, Player> playerMap = new HashMap<>();
    Round round;
    Player dealer;

    // Players added and removed from the game should be added on next round.
    public Player addPlayer(String ID, String playerName, int tokens) {
        Player player = new Player(ID, playerName, tokens);
        playerMap.put(ID, player);

        // If it's the first player, they're the dealer!
        if (playerMap.size() == 1) {
            setDealer(player);
        }

        LOGGER.info("Player added: " + player);
        LOGGER.info("Dealer is: " + dealer);

        return player;
    }

    // This will be called by the client on close
    public Player removePlayer(String ID) {
        Player player = playerMap.get(ID);
        return removePlayer(player);
    }

    // This will be called by the server, on deadbeat-has-no-money-left.
    public Player removePlayer(Player player) {
        LOGGER.info("Removing player: " + player);
        playerMap.remove(player.getID());

        if (dealer.equals(player) && playerMap.size() > 0) {
            LOGGER.info("Player was dealer, assigning new dealer... ");
            // Assign dealer to first player in map
            setDealer(playerMap.values().iterator().next());
        }

        if (round != null) {
            // Keeps the player 'in game' for points reasons before removing them at the end of the
            // round
            round.removePlayer(player);
        }
        return player;
    }

    private void setDealer(Player player) {
        dealer = player;
        LOGGER.info("Dealer set to: " + player);
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


}
