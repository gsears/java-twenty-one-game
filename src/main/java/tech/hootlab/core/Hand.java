package tech.hootlab.core;

/*
 * Hand.java Gareth Sears - 2493194S
 */

/**
 * A class which represents a players hand.
 */
public class Hand extends Deck implements Comparable<Hand> {

    int value = 0;

    /**
     * Adds a card to the deck, but also increments the value of the hand with it.
     */
    @Override
    public void add(Card card) {
        CardRanks rank = card.getRank();

        if (rank == CardRanks.ACE) {
            if (value < 11) {
                value += 11;
            } else {
                value += 1;
            }
        } else {
            value += rank.getValue();
        }

        super.add(card);
    }

    public int getValue() {
        return value;
    }

    /**
     * Allows easy comparisons with other hands based on value.
     */
    @Override
    public int compareTo(Hand o) {
        return value - o.getValue();
    }

}
