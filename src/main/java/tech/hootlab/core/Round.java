package tech.hootlab.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Round implements PropertyChangeObservable {

    // Constants
    // ---------
    private static final int HAND_MAXIMUM = 21;
    private static final int NUM_DEALT_CARDS = 2;

    // Observable events
    // -----------------
    public static final String STATE_CHANGE_EVENT = "ROUND_STATE_CHANGE";
    public static final String CURRENT_PLAYER_CHANGE_EVENT = "ROUND_CURRENT_PLAYER_CHANGE";
    public static final String DEALER_CHANGE_EVENT = "ROUND_DEALER_CHANGE";

    private PropertyChangeSupport propertyChangeSupport;

    // Round variables
    // ---------------
    // The list of players currently in the round.
    private List<Player> playerList = new LinkedList<>();
    // When players quit mid-game, they are added to this for skipping, token
    // handling, etc.
    private List<Player> removedPlayerList = new LinkedList<>();
    private RoundState state;
    private Player dealer;
    private Player currentPlayer;
    private Iterator<Player> playerTurnIterator;
    private int stake;
    private Deck deck;

    public Round() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    // Public Methods
    // --------------

    /**
     * Resets the round by doing the following:
     * <ul>
     * <li>Setting the dealer and stake each player is to put in.</li>
     * <li>Initialising player order so they are in sequence from the dealer.</li>
     * <li>Removing any current player state</li>
     * <li>Fetching a new deck of cards and shuffling it.</li>
     * <li>Setting the round state to 'READY'</li>
     * </ul>
     *
     * This puts the round in a 'ready' state which can be commenced upon the
     * dealer's 'deal' action.
     *
     * @param initialPlayerList The players from the model to be included in the
     *                          next round.
     * @param dealer            The dealer for the next round.
     * @param stake             The amount of tokens for each player's stake in this
     *                          round.
     */
    public void reset(List<Player> initialPlayerList, Player dealer, int stake) {
        this.dealer = dealer;
        this.stake = stake;
        this.playerList = orderPlayerList(initialPlayerList);
        this.playerTurnIterator = playerList.iterator();

        // No initial player (may not get a turn if natural 21)
        propertyChangeSupport.firePropertyChange(CURRENT_PLAYER_CHANGE_EVENT, currentPlayer, null);
        this.currentPlayer = null;

        this.deck = Deck.getStandardDeck().shuffle();
        setRoundState(RoundState.READY);
    }

    /**
     * This signals the start of a round. All the previous round's player attributes
     * are reset and cards are dealt.
     *
     * Checks are made to see if any 'natural 21s' result in winners / dealer
     * changes prior to normal round flow.
     */
    public void start() {
        // Player states and hands are cleared here and not on reset.
        // This allows any views to 'see' the results of the previous rounds.
        for (Player player : playerList) {
            player.clearHand();
            player.setStatus(PlayerState.PLAYING);
        }

        deal();
        setRoundState(RoundState.IN_PROGRESS);
        checkForDealWinners();
    }

    /**
     * Get the list of players participating in this round.
     *
     * This may not always be synchronised with the model, as users may connect but
     * to maintain round order integrity, they are only permitted to join at the
     * beginning of a new round.
     *
     * @return The list of players currently active in the round.
     */
    public List<Player> getPlayerList() {
        return playerList;
    }

    /**
     * This 'removes' a player from the round.
     *
     * Rather than disconnecting them immediately, it maintains their state so that
     * game flow is not interrupted and that their tokens can still be won / lost by
     * remaining players (no rage quitting!).
     *
     * @param player The player who has been removed from the model.
     */
    public void removePlayer(Player player) {
        removedPlayerList.add(player);

        if (player.equals(dealer) && state == RoundState.READY) {
            // Force deal to start if the dealer leaves before the game is started
            start();
        }

        // If player is current player, skip them.
        if (player.equals(currentPlayer)) {
            setNextPlayer();
        }
    }

    /**
     * 'Hits' with the current player.
     */
    public void hitWithCurrentPlayer() {

        // Add the top card to the player's hand.
        Card newCard = deck.deal();
        currentPlayer.addCardToHand(newCard);
        int handValue = currentPlayer.getHandValue();

        if (handValue > HAND_MAXIMUM) {
            // Player is 'bust'. Tokens go to the dealer.
            currentPlayer.setStatus(PlayerState.LOSER);
            currentPlayer.transferTokens(dealer, stake);
            setNextPlayer();
        } else if (handValue == HAND_MAXIMUM) {
            // Automatically skips on a 21.
            setNextPlayer();
        }
    }

    /**
     * 'Sticks' with the current player, goes on to the next player.
     */
    public void stickWithCurrentPlayer() {
        setNextPlayer();
    }

    // Private Methods
    // ---------------
    // Note: A lot of flow of control is kept internal to this class, with events
    // used to signal changes. This prevents 'contaminating' other classes with too
    // much 'twenty-one' logic, and allows them to focus on the overall game state /
    // communication.

    /**
     * Orders the player list, similar to starting from the dealer's left. This is
     * required for 'positional priority' if a new dealer is to be determined. And
     * it's classic blackjack...
     *
     * @param playerList The original list of players, out of 'round order'.
     * @return The list of players, from the dealer's left -> others -> dealer.
     */
    private List<Player> orderPlayerList(List<Player> playerList) {

        List<Player> orderedPlayerList = new LinkedList<>();

        int numPlayers = playerList.size();
        int dealerIndex = playerList.indexOf(dealer);

        // Cyclic array style
        for (int i = 1; i < numPlayers + 1; i++) {
            int playerIndex = (dealerIndex + i) % numPlayers;
            Player player = playerList.get(playerIndex);
            orderedPlayerList.add(player);
        }

        return orderedPlayerList;
    }

    /**
     * Sets the next player and checks for other conditions below.
     * <ul>
     * <li>If the player has been removed from the game, they are skipped.</li>
     *
     * <li>If the new player is the dealer, and everyone else is bust, the dealer
     * automatically wins and the end round state is triggered.</li>
     *
     * <li>If there are no more players, the end round state is triggered.</li>
     * </ul>
     */
    private void setNextPlayer() {

        if (playerTurnIterator.hasNext()) {
            Player previousPlayer = currentPlayer;
            currentPlayer = playerTurnIterator.next();
            propertyChangeSupport.firePropertyChange(CURRENT_PLAYER_CHANGE_EVENT, previousPlayer, currentPlayer);

            if (removedPlayerList.contains(currentPlayer)) {
                setNextPlayer();
            }

            // If the player is the dealer and there are no players left, end round
            if (currentPlayer.equals(dealer)) {
                boolean playersLeft = false;

                // Because player list is ordered, these will all be non-dealers.
                for (int i = 0; i < playerList.size() - 1; i++) {
                    if (playerList.get(i).getStatus() == PlayerState.PLAYING) {
                        playersLeft = true;
                        break;
                    }
                }

                // End round if the dealer is the only winner.
                if (!playersLeft) {
                    endRound();
                }
            }
        } else {
            // No players left.
            endRound();
        }
    }

    /**
     * Sets the round state, to alert listeners.
     *
     * @param state The new round state.
     */
    private void setRoundState(RoundState state) {
        propertyChangeSupport.firePropertyChange(STATE_CHANGE_EVENT, this.state, state);
        this.state = state;
    }

    /**
     * Deals 2 cards to each player as per standard.
     */
    private void deal() {
        for (Player player : playerList) {
            for (int i = 0; i < NUM_DEALT_CARDS; i++) {
                Card card = deck.deal();
                player.addCardToHand(card);
            }
        }
    }

    /**
     * Checks for winners after the deal.
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

                // Transfer double stakes to winner
                for (Player player : playerList) {
                    if (!player.equals(winner)) {
                        player.transferTokens(winner, stake * 2);
                        player.setStatus(PlayerState.LOSER);
                    }
                }
            }

            // If more than one winner on deal, no winners / losers or token transfer.

            // Regardless of number of winners, set the new dealer.
            if (!winnerList.contains(dealer)) {
                Player previousDealer = dealer;
                // Player with positional priority.
                dealer = winnerList.get(0);
                propertyChangeSupport.firePropertyChange(DEALER_CHANGE_EVENT, previousDealer, dealer);
            }

            setRoundState(RoundState.FINISHED);
        }
    }

    /**
     * End the round and do any token transfers.
     */
    private void endRound() {

        // Get players who are not 'bust'.
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
        } else {
            // Otherwise, compare to the dealer and pay accordingly
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

        setRoundState(RoundState.FINISHED);
    }

    // Set up property change observable functionality

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
