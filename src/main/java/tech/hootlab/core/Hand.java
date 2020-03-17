package tech.hootlab.core;

/*
 * Hand.java Gareth Sears - 2493194S
 */

/**
 * A class which represents a players hand.
 */
public class Hand extends Deck implements Comparable<Hand> {
    private static final long serialVersionUID = 1L;

    int value = 0;

    /**
     * Adds a card to the deck, but also increments the value of the hand with it.
     */
    @Override
    public void add(Card card) {
        CardRanks rank = card.getRank();
        value += rank.getValue();
        super.add(card);
    }

    public int getValue() {
        int aceAdjustedValue = value;
        if (value > 21) {
            // For each ace, subtract 10
            for (Card card : getCardList()) {
                if (card.getRank() == CardRanks.ACE) {
                    aceAdjustedValue -= 10;
                }
            }
        }
        return aceAdjustedValue;
    }

    /**
     * Allows easy comparisons with other hands based on value.
     */
    @Override
    public int compareTo(Hand o) {
        return value - o.getValue();
    }

}
