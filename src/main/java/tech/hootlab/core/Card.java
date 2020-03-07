package tech.hootlab.core;

/*
 * Card.java Gareth Sears - 2493194S
 */

/**
 * A class representing a playing card.
 */
public class Card {

    private CardSuits suit;
    private CardRanks rank;

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

}
