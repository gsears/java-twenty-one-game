package tech.hootlab.core;

import java.io.Serializable;

/*
 * Card.java Gareth Sears - 2493194S
 */

/**
 * A class representing a playing card.
 */
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CardSuits suit;
    private final CardRanks rank;

    public Card(CardSuits suit, CardRanks rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public CardSuits getSuit() {
        return suit;
    }

    public CardRanks getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return String.format("{%s of %s}", rank, suit);
    }
}
