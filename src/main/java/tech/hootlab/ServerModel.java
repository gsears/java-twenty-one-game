package tech.hootlab;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;

/*
 * Game.java Gareth Sears - 2493194S
 */

/**
 * A class representing the state of a game.
 */
public class ServerModel {
    private final static Logger LOGGER = Logger.getLogger(ServerModel.class.getName());

    public final int STAKE = 20;

    List<Player> playerList = new LinkedList<>();
    Round round = new Round();
    Player dealer;

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Round getRound() {
        return round;
    }

    public void startNextRound() {
        round.reset(new LinkedList<>(playerList), dealer, STAKE);
    }

    public Player getDealer() {
        return dealer;
    }

    // Players added and removed from the game should be added on next round.
    public void addPlayer(Player player) {
        playerList.add(player);
        LOGGER.info("Player added: " + player);

        // If it's the first player, they're the dealer!
        if (playerList.size() == 1) {
            setDealer(player);
        }

        // We've got enough players to play
        if (playerList.size() == 2) {
            LOGGER.info("We have two players");
            startNextRound();
        }
    }

    public void removePlayer(String ID) {
        for (Player player : playerList) {
            if (player.getID().equals(ID)) {
                removePlayer(player);
            }
        }
    }

    public void removePlayer(Player player) {
        LOGGER.info("Removing player: " + player);
        playerList.remove(player);

        if (dealer.equals(player) && playerList.size() > 0) {
            LOGGER.info("Player was dealer, assigning new dealer... ");
            setDealer(playerList.get(0));
        }

        if (round != null) {
            round.removePlayer(player); // Handled separately for scoring and logic reasons
        }
    }

    private void setDealer(Player player) {
        dealer = player;
        LOGGER.info("Dealer set to: " + player);
    }
}
