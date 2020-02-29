package tech.hootlab;

import java.util.LinkedList;
import java.util.List;

/*
 * Game.java Gareth Sears - 2493194S
 */

/**
 * A class representing the state of a game.
 */
public class GameModel {

    public final int INITIAL_TOKENS = 200;
    public final int STAKE = 20;

    boolean isGameActive = false;
    // Game deck
    Deck deck;
    // Players
    List<Player> playerList = new LinkedList<>();
    // Round
    Round round;
    // Dealer
    Player dealer;


    public GameModel() {
        reset();
    }

    private void reset() {
        deck = Deck.getStandardDeck();
        deck.shuffle();
    }

    public void addPlayer(Player player) {
        playerList.add(player);

    }

    // TODO: Think about how to dynamically add and remove players from the game,
    // and what happens when they are removed.
    public void removePlayer(Player player) {
        playerList.remove(player);
    }
}
