package tech.hootlab;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

public class ServerModel {

    private final static Logger LOGGER = Logger.getLogger(ServerModel.class.getName());

    public final int ROUND_STAKE = 20;

    List<Player> playerList = new LinkedList<>();
    Round round = new Round();

    // Store a reference to the current dealer and next dealer.
    // If a dealer leaves the game, play automatically continues until the next round when.
    Player dealer;

    public List<Player> getRoundPlayerList() {
        return round.getPlayerList();
    }

    public List<Player> getLobbyPlayerList() {
        return playerList;
    }

    // Players added and removed from the game should be added on next round.
    public void addPlayer(Player player) {
        synchronized (playerList) {
            playerList.add(player);

            // If it's the first player, they're the dealer!
            if (playerList.size() == 1) {
                setDealer(player);
            }

            // We've got enough players to play
            if (playerList.size() == 2) {
                startNextRound();
            }
        }
    }

    public void removePlayer(String ID) {
        synchronized (playerList) {
            // Players are comparable by ID
            Player playerToRemove = null;
            Iterator<Player> iterator = playerList.iterator();
            while (playerToRemove == null && iterator.hasNext()) {
                Player player = iterator.next();
                if (ID.equals(player.getID())) {
                    playerToRemove = player;
                }
            }
            removePlayer(playerToRemove);
        }
    }

    public void removePlayer(Player player) {
        LOGGER.info("Removing player from model");
        synchronized (playerList) {
            playerList.remove(player);

            if (dealer.equals(player) && playerList.size() > 0) {
                dealer = playerList.get(0);
            }
        }

        // This needs to be done here because it is contingent on the dealer
        if (round != null) {
            LOGGER.info("Removing player from round");
            round.removePlayer(player); // Handled separately for scoring and logic reasons
        }

        // Restart round if finished...
        if (round.getState() == RoundState.FINISHED && playerList.size() > 1) {
            startNextRound();
        }
    }

    public List<Player> removeBrokePlayers() {
        List<Player> eliminatedPlayers = new LinkedList<>();
        synchronized (playerList) {
            for (Player player : playerList) {
                if (player.getTokens() == 0) {
                    eliminatedPlayers.add(player);
                }
            }

            eliminatedPlayers.forEach(player -> removePlayer(player));
        }

        // Return immutable list for thread safety
        return Collections.unmodifiableList(eliminatedPlayers);

    }

    public Player getDealer() {
        return dealer;
    }

    public void setDealer(Player player) {
        dealer = player;
        LOGGER.info("Next dealer is: " + player);
    }

    // Gameplay functions

    public void startNextRound() {
        // Send an immutable version of the current player list to initialise a round
        round.reset(Collections.unmodifiableList(playerList), dealer, ROUND_STAKE);
    }

    public void deal() {
        round.start();
    }

    public void hitWithCurrentPlayer() {
        round.hitWithCurrentPlayer();
    }

    public void stickWithCurrentPlayer() {
        round.stickWithCurrentPlayer();
    }

    // Add Round listeners here
    public void addRoundPropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        round.addPropertyChangeListener(propertyName, pcl);
    }

    public void removeRoundPropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        round.removePropertyChangeListener(propertyName, pcl);
    }
}
