package tech.hootlab.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Round implements PropertyChangeObservable {
    private final static Logger LOGGER = Logger.getLogger(Round.class.getName());

    private static final int HAND_MAXIMUM = 21;
    private static final int NUM_DEALT_CARDS = 2;

    // Observable attributes
    public static final String STATE_CHANGE_EVENT = "ROUND_STATE_CHANGE";
    public static final String CURRENT_PLAYER_CHANGE_EVENT = "ROUND_CURRENT_PLAYER_CHANGE";
    public static final String DEALER_CHANGE_EVENT = "ROUND_DEALER_CHANGE";
    private PropertyChangeSupport propertyChangeSupport;

    private List<Player> removedPlayerList = new LinkedList<>();
    private List<Player> playerList = new LinkedList<>();
    private RoundState state;
    private Player dealer;
    private Player currentPlayer;
    private Iterator<Player> playerTurnIterator;
    private int stake;
    private Deck deck;

    public Round() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void reset(List<Player> initialPlayerList, Player dealer, int stake) {
        LOGGER.info("Round reset");
        this.dealer = dealer;
        this.stake = stake;
        this.playerList = initPlayerList(initialPlayerList);
        this.playerTurnIterator = playerList.iterator();
        // No initial player (may not get a turn if natural 21)
        this.currentPlayer = null;
        this.deck = Deck.getStandardDeck().shuffle();
        setState(RoundState.READY);
    }

    // Start the round
    public void start() {
        LOGGER.info("Round started");
        deal();
        setState(RoundState.IN_PROGRESS);
        checkForDealWinners();
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void removePlayer(Player player) {
        // Keep the player 'in game' (their tokens are up for grabs),
        // just ensure they are skipped.

        // If player is current player, skip them.
        if (player.equals(currentPlayer)) {
            setNextPlayer();
        }
        if (player.equals(dealer) && state == RoundState.READY) {
            // Force deal to start if the dealer leaves before the game is started
            start();
        } else {
            removedPlayerList.add(player);
        }

    }

    // CurrentPlayerHit
    public void hitWithCurrentPlayer() {

        if (state != RoundState.IN_PROGRESS) {
            throw new IllegalStateException("Round not in progess");
        }

        Card newCard = deck.deal();
        currentPlayer.addCardToHand(newCard);
        int handValue = currentPlayer.getHandValue();

        if (handValue > HAND_MAXIMUM) {
            currentPlayer.setStatus(PlayerState.LOSER);
            currentPlayer.transferTokens(dealer, stake);
        } else if (handValue == HAND_MAXIMUM) {
            setNextPlayer();
        }
    }

    public void stickWithCurrentPlayer() {

        if (state != RoundState.IN_PROGRESS) {
            throw new IllegalStateException("Round not in progess");
        }

        setNextPlayer();
    }

    // PRIVATE METHODS
    // Flow of control is internal. Listeners will know when to query state.

    private List<Player> initPlayerList(List<Player> playerList) {

        List<Player> orderedPlayerList = new LinkedList<>();

        int numPlayers = playerList.size();
        int dealerIndex = playerList.indexOf(dealer);

        // Start counting from the dealer's 'left' for positional priority later
        for (int i = 1; i < numPlayers + 1; i++) {
            int playerIndex = (dealerIndex + i) % numPlayers;

            Player player = playerList.get(playerIndex);
            orderedPlayerList.add(player);

            player.setStatus(PlayerState.PLAYING);
        }

        return orderedPlayerList;
    }

    private void setNextPlayer() {

        if (playerTurnIterator.hasNext()) {
            Player previousPlayer = currentPlayer;
            currentPlayer = playerTurnIterator.next();
            propertyChangeSupport.firePropertyChange(CURRENT_PLAYER_CHANGE_EVENT, previousPlayer,
                    currentPlayer);

            // If the player has been removed during the round, recur until a next one is found
            // or we die looking.
            if (removedPlayerList.contains(currentPlayer)) {
                setNextPlayer();
            }

        } else {
            endRound();
        }
    }

    private void setState(RoundState state) {
        propertyChangeSupport.firePropertyChange(STATE_CHANGE_EVENT, this.state, state);
        this.state = state;
    }

    private void deal() {
        LOGGER.info("Dealing...");
        for (Player player : playerList) {
            // Two cards each is standard...
            for (int i = 0; i < NUM_DEALT_CARDS; i++) {
                Card card = deck.deal();
                LOGGER.info("Card dealt: " + card);
                player.addCardToHand(card);

            }
        }
    }

    /**
     * Checks for winners after the deal.
     *
     * @return Null if there are no winners, otherwise the dealer for the next round.
     */
    private void checkForDealWinners() {

        List<Player> winnerList = new LinkedList<>();

        for (Player player : playerList) {
            if (player.getHandValue() == HAND_MAXIMUM) {
                player.setStatus(PlayerState.WINNER);
                winnerList.add(player);
            }
        }

        int numWinners = winnerList.size();

        // No winners on deal
        if (numWinners == 0) {
            setNextPlayer();
        } else {
            // One winner on deal
            if (numWinners == 1) {
                Player winner = winnerList.get(0);

                for (Player player : playerList) {
                    if (!player.equals(winner)) {
                        player.transferTokens(winner, stake * 2);
                        player.setStatus(PlayerState.LOSER);
                    }
                }

                // More than one winner on deal
            } else {
                if (!winnerList.contains(dealer)) {
                    Player previousDealer = dealer;
                    // dealer is player with positional priority.
                    dealer = winnerList.get(0);
                    propertyChangeSupport.firePropertyChange(DEALER_CHANGE_EVENT, previousDealer,
                            dealer);
                }
                // No winners / losers, so no money is exchanged / no tokenChangeUpdates
            }

            setState(RoundState.FINISHED);
        }
    }

    private void endRound() {

        // Get remaining players
        List<Player> remainingPlayers = new LinkedList<>();

        for (Player player : playerList) {
            if (player.getStatus() == PlayerState.PLAYING) {
                remainingPlayers.add(player);
            }
        }

        // If dealer goes over 21, he pays remaining winners
        if (dealer.getStatus() == PlayerState.LOSER) {
            for (Player player : remainingPlayers) {
                dealer.transferTokens(player, stake);
                player.setStatus(PlayerState.WINNER);
            }
        } else { // Otherwise, compare to the dealer and pay accordingly
            for (Player player : remainingPlayers) {
                int comparison = player.getHand().compareTo(dealer.getHand());
                if (comparison < 0) {
                    player.transferTokens(dealer, stake);
                    player.setStatus(PlayerState.LOSER);
                } else if (comparison > 0) {
                    dealer.transferTokens(player, stake);
                    player.setStatus(PlayerState.WINNER);
                }
            }
        }

        setState(RoundState.FINISHED);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, pcl);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, pcl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }

}
