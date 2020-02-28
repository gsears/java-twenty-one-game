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

    boolean isGameActive = false;

    Deck deck;

    // Players
    List<Player> playerList = new LinkedList<>();
    Player dealer;
    Player currentPlayer;

    public GameModel() {
        reset();
    }

    private void reset() {
        deck = Deck.getStandardDeck();
        deck.shuffle();
    }

    public void addPlayer(Player player) {
        playerList.add(player);
        if (playerList.size() == 1) {
            dealer = currentPlayer;
        } else {
            isGameActive = true;
            if (currentPlayer == null) {
                currentPlayer = player;
            }
        }
    }

    // TODO: Think about how to dynamically add and remove players from the game,
    // and what happens when they are removed.
    public void removePlayer(Player player) {
        playerList.remove(player);
        if (playerList.size() == 1) {
            isGameActive = false;
            dealer = currentPlayer;
        }
    }
}
