package tech.hootlab;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

public class ServerModel {

    List<Player> lobbyPlayerList = new LinkedList<>();

    // Due to its complexity and interrelated state, this is NOT thread safe.
    // It's locked down in this class.
    private Round round = new Round();

    private final int stake;

    // Store a reference to the current dealer and next dealer.
    // If a dealer leaves the game, play automatically continues until the next round when.
    private Player dealer;
    private final Object dealerLock = new Object();

    public ServerModel(int stake) {
        this.stake = stake;
    }

    public List<Player> getRoundPlayerList() {
        // Return read only list from round. Internal players are thread-safe.
        // (Round deals with its own state and is confined in this class).
        return Collections.unmodifiableList(round.getPlayerList());
    }

    public List<Player> getLobbyPlayerList() {
        // Return read only list. Internal players are thread-safe.
        return Collections.unmodifiableList(lobbyPlayerList);
    }

    // Players added and removed from the game should be added on next round.
    public void addPlayer(Player player) {
        synchronized (lobbyPlayerList) {
            lobbyPlayerList.add(player);

            // If it's the first player, they're the dealer!
            if (lobbyPlayerList.size() == 1) {
                setDealer(player);
            }

            // We've got enough players to play
            if (lobbyPlayerList.size() == 2) {
                startNextRound();
            }
        }
    }

    public void removePlayer(String ID) {
        synchronized (lobbyPlayerList) {
            // Players are comparable by ID
            Player playerToRemove = null;
            Iterator<Player> iterator = lobbyPlayerList.iterator();
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
        synchronized (lobbyPlayerList) {
            lobbyPlayerList.remove(player);
            int lobbySize = lobbyPlayerList.size();
            if (dealer.equals(player) && lobbySize > 0) {
                dealer = lobbyPlayerList.get(0);
            }

            synchronized (round) {
                // RoundPlayer removals handled separately for scoring and logic reasons
                round.removePlayer(player);

                // Restart round if finished...
                // Stays in lock because this is contingent on round size
                if (round.getState() == RoundState.FINISHED && lobbyPlayerList.size() > 1) {
                    startNextRound();
                }
            }
        }
    }

    public List<Player> removeBrokePlayers() {
        List<Player> eliminatedPlayers = new LinkedList<>();

        synchronized (lobbyPlayerList) {
            for (Player player : lobbyPlayerList) {
                if (player.getTokens() == 0) {
                    eliminatedPlayers.add(player);
                }
            }
            eliminatedPlayers.forEach(player -> removePlayer(player));
        }

        // Return immutable list for safety
        return Collections.unmodifiableList(eliminatedPlayers);

    }

    public Player getDealer() {
        synchronized (dealerLock) {
            return dealer;
        }
    }

    public void setDealer(Player player) {
        synchronized (dealerLock) {
            dealer = player;
        }
    }

    // Gameplay functions

    public void startNextRound() {
        // Passes lobbyPlayerList as unmodifiable, just in case any future round changes risk
        // mutation.
        synchronized (round) {
            round.reset(Collections.unmodifiableList(lobbyPlayerList), dealer, stake);
        }
    }

    public void deal() {
        synchronized (round) {
            round.start();
        }
    }

    public void hitWithCurrentPlayer() {
        synchronized (round) {
            round.hitWithCurrentPlayer();
        }
    }

    public void stickWithCurrentPlayer() {
        synchronized (round) {
            round.stickWithCurrentPlayer();
        }
    }

    // Add Round listeners here
    // These are done in the model constructor, so don't need thread safety yet.
    public void addRoundPropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        round.addPropertyChangeListener(propertyName, pcl);
    }

    public void removeRoundPropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        round.removePropertyChangeListener(propertyName, pcl);
    }
}
