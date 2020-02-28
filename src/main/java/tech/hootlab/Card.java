package tech.hootlab;

/*
 * Card.java Gareth Sears - 2493194S
 */

/**
 * A class representing a playing card.
 */
public class Card {

    private Suits suit;
    private Ranks rank;

    public Card(Suits suit, Ranks rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suits getSuit() {
        return suit;
    }

    public Ranks getRank() {
        return rank;
    }

}
