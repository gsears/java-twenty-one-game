package tech.hootlab.core;

import java.util.LinkedList;
import java.util.List;

public class Round implements RoundObservable {

    // Hand maximum
    private static final int HAND_MAXIMUM = 21;

    // Listeners
    private List<RoundEventListener> roundEventListenerList = new LinkedList<>();

    private RoundState state = RoundState.FINISHED;

    private int stake;

    private int numPlayers;
    private List<Player> playerList;

    private int dealerIndex;
    private Player dealer;

    private int currentPlayerIndex;
    private Player currentPlayer;

    private List<Player> winnerList;
    private List<Player> loserList;

    private Deck deck = Deck.getStandardDeck();

    Round(List<Player> playerList, Player dealer, int stake) {
        this.numPlayers = playerList.size();
        this.playerList = playerList;
        this.dealer = dealer;
        this.stake = stake;

        dealerIndex = playerList.indexOf(dealer);
        currentPlayerIndex = (dealerIndex + 1) % numPlayers;
        currentPlayer = playerList.get(currentPlayerIndex);

        winnerList = new LinkedList<>();
        loserList = new LinkedList<>();

        deck.shuffle();
        deal();

        setState(RoundState.IN_PROGRESS);

        checkForDealWinners();
    }

    public RoundState getState() {
        return state;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    // CurrentPlayerHit
    public void hitWithCurrentPlayer() {
        Card newCard = deck.deal();
        Hand playerHand = currentPlayer.getHand();

        playerHand.add(newCard);
        notifyRoundCardChange();

        int handValue = playerHand.getValue();

        if (handValue > HAND_MAXIMUM) {
            loserList.add(currentPlayer);
            playerList.remove(currentPlayer);
            currentPlayer.transferTokens(dealer, stake);
            notifyRoundTokenChange();
        } else if (handValue == HAND_MAXIMUM) {
            setNextPlayer();
        }

    }

    public void stickWithCurrentPlayer() {
        setNextPlayer();
    }

    public List<Player> getWinnerList() {
        return winnerList;
    }

    public List<Player> getLoserList() {
        return loserList;
    }

    // PRIVATE METHODS
    // Flow of control is internal. Listeners will know when to query state.

    private void setNextPlayer() {
        if (currentPlayer.equals(dealer)) {
            endRound();
            setState(RoundState.FINISHED);
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
        // Two cards each
        for (int i = 1; i <= numPlayers * 2; i++) {
            // Deal one card by one starting from the left of the dealer
            int playerIndex = (i + dealerIndex) % numPlayers;
            Card card = deck.deal();
            Hand playerHand = playerList.get(playerIndex).getHand();
            playerHand.add(card);
        }

        // Not necessary because the round state is initialised on start...
        // This will update all player's hands...
        // notifyRoundCardChange();
    }

    /**
     * Checks for winners after the deal.
     *
     * @return Null if there are no winners, otherwise the dealer for the next round.
     */
    private void checkForDealWinners() {

        // Add any players who have 21 to the winner list
        for (int i = 1; i < numPlayers + 1; i++) {

            // Start counting from the dealer's 'left' for positional priority later
            int playerIndex = (dealerIndex + i) % numPlayers;
            Player player = playerList.get(playerIndex);

            if (player.getHand().getValue() == HAND_MAXIMUM) {
                winnerList.add(player);
            }
        }

        if (winnerList.size() == 0) {
            // Listeners can safely fetch current player to start playing
            notifyRoundPlayerChange();

        } else {

            // If there is a single winner...
            if (winnerList.size() == 1) {
                Player winner = winnerList.get(0);

                // Update the loserlist
                loserList = new LinkedList<>(playerList);
                loserList.remove(winner);

                // Transfer double stake to winner.
                for (Player loser : loserList) {
                    loser.transferTokens(winner, stake * 2);
                }

                // Tokens have changed hands...
                notifyRoundTokenChange();

            } else {

                if (!winnerList.contains(dealer)) {
                    // dealer is player with positional priority.
                    dealer = winnerList.get(0);
                }
                // No losers, so no money is exchanged / no tokenChangeUpdates
            }

            setState(RoundState.FINISHED);
        }
    }

    private void endRound() {
        // Calculate winners / losers in the game
        for (Player player : playerList) {
            int comparison = player.getHand().compareTo(dealer.getHand());
            if (comparison < 0) {
                player.transferTokens(dealer, stake);
                loserList.add(player);
            } else if (comparison > 0) {
                dealer.transferTokens(player, stake);
                winnerList.add(player);
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
