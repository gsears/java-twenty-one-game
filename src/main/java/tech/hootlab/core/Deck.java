package tech.hootlab.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/*
 * Deck.java Gareth Sears - 2493194S
 */

/**
 * A class representing a collection of cards. Note: this doesn't necessarily mean the full '52'
 * cards, rather a 'collection' of cards of any size.
 */
public class Deck implements Serializable {
    private static final long serialVersionUID = 1L;

    // Define as linked list so we can use list (shuffle) and queue (poll)
    // functionality.

    // This is NOT thread safe, so all operations relating to this object must
    // be via this class and synchronized against this object.
    // This list should ONLY be returned as an immutable version. I imagine this is
    // OK, as the game is turn based and each thread is responsible for updates only
    // on it's own turn, so everything should be eventually consistant in the right
    // timeframe.
    LinkedList<Card> cardList = new LinkedList<>();

    /**
     * A static utility method for getting your bog-standard deck of cards from the ranks and suits
     * provided.
     *
     * @return
     */
    public static Deck getStandardDeck() {
        // Shouldn't need to be synchronized, as the object is initialized completely in this block
        Deck deck = new Deck();
        for (CardSuits suit : CardSuits.values()) {
            for (CardRanks rank : CardRanks.values()) {
                deck.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    /**
     * Get a list of the cards. This has to return a linked list for serializability (was previously
     * getting blank lists on the receiving end).
     *
     * @return a linked list of the cards
     */
    public List<Card> getCardList() {
        // Returns immutable list for thread safety
        return Collections.unmodifiableList(cardList);
    }

    /**
     * Shuffle the deck.
     */
    public Deck shuffle() {
        // Lock the list so it cannot be read/written to during shuffle
        synchronized (cardList) {
            Collections.shuffle(cardList);
        }
        return this;
    }

    /**
     * Takes the top card off the deck and returns it.
     *
     * @return
     */
    public Card deal() {
        // Lock list
        synchronized (cardList) {
            return cardList.poll();
        }
    }

    /**
     * Add a single card to the bottom of this deck.
     *
     * @param card The card to add.
     */
    public void add(Card card) {
        synchronized (cardList) {
            cardList.addLast(card);
        }
    }

    /**
     * Add a deck to the bottom of this deck.
     *
     * @param deck The deck to add.
     */
    public void add(Deck deck) {
        synchronized (cardList) {
            cardList.addAll(deck.getCardList());
        }
    }
}
