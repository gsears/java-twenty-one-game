package tech.hootlab;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * Deck.java Gareth Sears - 2493194S
 */

/**
 * A class representing a deck of cards.
 */
public class Deck {
    List<Card> cardList = new LinkedList<>();

    /**
     * Create a new, unshuffled deck...fresh from the packet.
     */
    public Deck() {
        initialise();
    }

    /**
     * Populate the deck with cards from ranks and suits.
     */
    private void initialise() {
        for (Suits suit : Suits.values()) {
            for (Ranks rank : Ranks.values()) {
                cardList.add(new Card(suit, rank));
            }
        }
    }

    public List<Card> getCardList() {
        return cardList;
    }

    /**
     * Shuffle the deck.
     */
    public void shuffle() {
        Collections.shuffle(cardList);
    }
}
