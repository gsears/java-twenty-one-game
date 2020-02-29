package tech.hootlab;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Round {

    int stake;
    List<Player> playerList;
    int numPlayers;
    int dealerPosition;
    Player dealer;
    int currentPlayerIndex;
    Deck deck;

    Round(List<Player> playerList, Player dealer, Deck deck, int stake) {
        this.numPlayers = playerList.size();
        this.playerList = playerList;
        this.dealer = dealer;
        this.deck = deck;
        this.stake = stake;

        reset();
    }

    public void reset() {
        dealerPosition = playerList.indexOf(dealer);
        currentPlayerIndex = (dealerPosition + 1) % playerList.size();
        deck.shuffle();
        deal();
    }

    private void deal() {

        for (int i = 0; i < numPlayers * 2; i++) {
            // Proper dealing :P
            int playerIndex = (i + currentPlayerIndex) % numPlayers;
            Card card = deck.deal();
            Hand playerHand = playerList.get(playerIndex).getHand();
            playerHand.add(card);
        }
    }

    /**
     * Checks for winners after the deal.
     *
     * @return Null if there are no winners, otherwise the dealer for the next round.
     */
    private Player checkForDealWinners() {
        List<Player> winners = new LinkedList<>();

        // Add any players who have 21 to the winner list
        for (int i = 1; i < numPlayers + 1; i++) {

            // Start counting from the dealer's 'left' for positional priority if needed later
            int playerIndex = (dealerPosition + i) % numPlayers;
            Player player = playerList.get(playerIndex);

            if (player.getHand().getValue() == 21) {
                winners.add(player);
            }
        }

        if (winners.size() == 0) {
            // Return null for no winners
            return null;
        } else if (winners.size() == 1) {
            // If there is a single winner...
            Player winner = winners.get(0);

            // Get loser list...
            List<Player> loserList = new LinkedList<>(playerList);
            loserList.remove(winner);

            // Transfer double stake to winner.
            for (Player loser : loserList) {
                loser.transferTokens(stake * 2, winner);
            }
            return winner;
        } else {
            // If there is more than one winner
            // No payout...
            if (winners.contains(dealer)) {
                // Return the existing dealer if they won.
                return dealer;
            } else {
                // Otherwise, return winner with positional priority.
                return winners.get(0);
            }
        }
    }



}
