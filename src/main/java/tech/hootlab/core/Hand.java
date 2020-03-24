package tech.hootlab.core;

/*
 * Hand.java Gareth Sears - 2493194S
 */

/**
 * A class which represents a players hand.
 */
public class Hand extends Deck implements Comparable<Hand> {
    private static final long serialVersionUID = 1L;

    private static final Object tieLock = new Object();
    private int value = 0;

    /**
     * Adds a card to the deck, but also increments the value of the hand with it.
     */
    @Override
    public void add(Card card) {
        CardRanks rank = card.getRank();
        synchronized (this) {
            value += rank.getValue();
        }
        super.add(card);
    }

    public int getValue() {
        // Could have synchronized whole method, but haven't for consistency
        // Whole calculation relies on consistent value, thus lock.
        synchronized (this) {
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
    }

    /**
     * Allows easy comparisons with other hands based on value.
     */
    @Override
    public int compareTo(Hand otherHand) {

        // Because of nested synchronised statements, we use hashCodes to
        // enforce an order on the locks. This prevents deadlocks from two
        // comparisons happening simultaneously but with reversed objects.
        // If there is a hash conflict, the static tielock ensures no deadlocks.
        int thisHash = System.identityHashCode(this);
        int otherHash = System.identityHashCode(otherHand);

        if (thisHash < otherHash) {
            synchronized (this) {
                synchronized (otherHand) {
                    return value - otherHand.getValue();
                }
            }
        } else if (thisHash > otherHash) {
            synchronized (otherHand) {
                synchronized (this) {
                    return value - otherHand.getValue();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (this) {
                    synchronized (otherHand) {
                        return value - otherHand.getValue();
                    }
                }
            }
        }
    }
}
