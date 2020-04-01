package tech.hootlab;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import tech.hootlab.core.Player;
import tech.hootlab.core.Round;
import tech.hootlab.core.RoundState;

/*
 * ServerModel.java
 *
 * Gareth Sears - 2493194S
 *
 * This class acts as an intermediary between the round (game logic) and the various players
 * (connections). It ensures appropriate locking on the Round object and its state is designed to
 * act as a 'lobby', so when players connect they join at the start of the the next round. This
 * avoids tricky ordering problems between players.
 *
 * It also manages round and dealer state when players connect / disconnect.
 *
 * It is designed to be threadsafe, as it is shared between multiple client connections. Thus, it
 * only returns immutable lists for processing (which in turn contain only threadsafe classes).
 */
public class ServerModel {

    private List<Player> lobbyPlayerList = new LinkedList<>();

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

    public List<Player> getPlayersInRound() {
        // Return read only list from round. Internal players are thread-safe.
        // (Round deals with its own state and is confined in this class).
        return Collections.unmodifiableList(round.getPlayerList());
    }

    public List<Player> getPlayersInLobby() {
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
                initialiseNextRound();
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
        if (player != null) {
            synchronized (lobbyPlayerList) {
                lobbyPlayerList.remove(player);
                int lobbySize = lobbyPlayerList.size();
                if (dealer.equals(player) && lobbySize > 0) {
                    dealer = lobbyPlayerList.get(0);
                }

                // RoundPlayer removals handled separately, to ensure the player
                // 'remains' until the end of the round, though their actions are
                // automated. This avoids rage quitting.

                synchronized (round) {
                    round.removePlayer(player);
                    // Restart round if finished...
                    // Stays in lock because this is contingent on round size
                    if (round.getState() == RoundState.FINISHED && lobbyPlayerList.size() > 1) {
                        initialiseNextRound();
                    }
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

            for (Player eliminatedPlayer : eliminatedPlayers) {
                removePlayer(eliminatedPlayer);
            }
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

    public void initialiseNextRound() {
        // Passes lobbyPlayerList as unmodifiable, just in case any future round changes risk
        // mutation.
        synchronized (lobbyPlayerList) {
            // Starts new game if lobby is greater than 1
            if (lobbyPlayerList.size() > 1) {
                synchronized (round) {
                    round.reset(Collections.unmodifiableList(lobbyPlayerList), dealer, stake);
                }
            }
        }
    }

    public void startRound() {
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
