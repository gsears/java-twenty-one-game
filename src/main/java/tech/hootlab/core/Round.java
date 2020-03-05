package tech.hootlab.core;

import java.util.LinkedList;
import java.util.List;

public class Round implements RoundObservable {

    // Hand maximum
    private static final int HAND_MAXIMUM = 21;
    private static final int NUM_DEALT_CARDS = 2;

    // Listeners
    private List<RoundEventListener> roundEventListenerList = new LinkedList<>();

    private List<Player> playerList;
    private RoundState state;
    private Player dealer;
    private int stake;
    private int currentPlayerIndex;
    private Player currentPlayer;
    private Deck deck;

    Round(List<Player> initialPlayerList, Player dealer, int stake) {

        this.dealer = dealer;
        this.stake = stake;
        this.playerList = initPlayerList(initialPlayerList);

        // No initial player (may not get a turn if natural 21)
        this.currentPlayerIndex = -1;
        this.currentPlayer = null;

        this.deck = Deck.getStandardDeck().shuffle();

        setState(RoundState.READY);
    }

    public RoundState getState() {
        return state;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    // Start the round
    public void start() {
        deal();
        setState(RoundState.IN_PROGRESS);
        checkForDealWinners();
    }

    // CurrentPlayerHit
    public void hitWithCurrentPlayer() {
        Card newCard = deck.deal();
        Hand playerHand = currentPlayer.getHand();

        playerHand.add(newCard);
        notifyRoundCardChange();

        int handValue = playerHand.getValue();

        if (handValue > HAND_MAXIMUM) {

            currentPlayer.setStatus(PlayerState.LOSER);
            currentPlayer.transferTokens(dealer, stake);
            notifyRoundTokenChange();

        } else if (handValue == HAND_MAXIMUM) {
            setNextPlayer();
        }

    }

    public void stickWithCurrentPlayer() {
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

            // Set status to waiting
            player.setStatus(PlayerState.WAITING);
        }

        return orderedPlayerList;
    }

    private void setNextPlayer() {
        if (currentPlayer.equals(dealer)) {
            endRound();
        } else {
            currentPlayer = playerList.get(currentPlayerIndex++);
            notifyRoundPlayerChange();
        }
    }

    private void setState(RoundState state) {
        this.state = state;
        notifyRoundStateChange();
    }

    private void deal() {
        for (Player player : playerList) {
            for (int i = 0; i < NUM_DEALT_CARDS; i++) {
                Card card = deck.deal();
                Hand playerHand = player.getHand();
                playerHand.add(card);
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
            if (player.getHand().getValue() == HAND_MAXIMUM) {
                player.setStatus(PlayerState.WINNER);
                winnerList.add(player);
            }
        }

        int numWinners = winnerList.size();

        if (numWinners == 0) { // No winners on deal
            setNextPlayer();
        } else {
            if (numWinners == 1) { // One winner on deal

                Player winner = winnerList.get(0);

                for (Player player : playerList) {
                    if (!player.equals(winner)) {
                        player.transferTokens(winner, stake * 2);
                        player.setStatus(PlayerState.LOSER);
                    }
                }

            } else { // More than one winner on deal
                if (!winnerList.contains(dealer)) {
                    // dealer is player with positional priority.
                    dealer = winnerList.get(0);
                }
                // No winners / losers, so no money is exchanged / no tokenChangeUpdates
            }

            notifyRoundTokenChange();
            setState(RoundState.FINISHED);
        }
    }

    private void endRound() {

        // Get remaining players
        List<Player> remainingPlayers = new LinkedList<>();

        for (Player player : playerList) {
            if (player.getStatus() == PlayerState.WAITING) {
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

        notifyRoundTokenChange();
        setState(RoundState.FINISHED);
    }

    // Listener Methods

    @Override
    public void addRoundEventListener(RoundEventListener roundEventListener) {
        roundEventListenerList.add(roundEventListener);
    }

    @Override
    public void removeRoundEventListener(RoundEventListener roundEventListener) {
        roundEventListenerList.remove(roundEventListener);
    }

    public void notifyRoundEventListeners(RoundEvent event) {
        roundEventListenerList.stream().forEach(l -> l.roundEventReceived(event));
    }

    // TODO: Set only to PlayerStateChange() and pass players
    // TODO: Set only to RoundStateChange() and pass info (current player / dealer)

    public void notifyRoundStateChange() {
        notifyRoundEventListeners(RoundEvent.STATE_CHANGED);
    }

    public void notifyRoundPlayerChange() {
        notifyRoundEventListeners(RoundEvent.PLAYER_CHANGED);
    }

    public void notifyRoundCardChange() {
        notifyRoundEventListeners(RoundEvent.CARD_CHANGED);
    }

    public void notifyRoundTokenChange() {
        notifyRoundEventListeners(RoundEvent.TOKEN_CHANGED);
    }

}
