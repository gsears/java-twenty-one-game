package tech.hootlab;

import java.util.LinkedList;
import java.util.List;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;

/*
 * Game.java Gareth Sears - 2493194S
 */

/**
 * A class representing the state of a game.
 */
public class GameModel {

    public final int INITIAL_TOKENS = 200;
    public final int STAKE = 20;

    // Players
    List<Player> playerList = new LinkedList<>();
    // Round
    Round round;
    // Dealer
    Player dealer;

    // Players added and removed from the game should be added on next round.
    public void addPlayer(Player player) {
        playerList.add(player);

    }

    public void removePlayer(Player player) {
        playerList.remove(player);
    }
}
