package tech.hootlab.core;

/*
 * Ranks.java Gareth Sears - 2493194S
 */

/**
 * An enum representing the different possible card ranks.
 */
public enum CardRanks {
    ACE(11), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(
            10), QUEEN(10), KING(10);

    private int val;

    private CardRanks(int val) {
        this.val = val;
    }

    /**
     * Returns the card value. Note that ACE defaults to 11 and the 'royals' to 10.
     *
     * @return The card value
     */
    public int getValue() {
        return val;
    }

}
