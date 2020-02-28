/*
 * DeckTest.java Gareth Sears - 2493194S
 */
package tech.hootlab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeckTest {

    @DisplayName("Deck has 52 cards")
    @Test
    void deckCanInitialise() {
        Deck deck = new Deck();
        assertEquals(52, deck.getCardList().size());
    }

    @DisplayName("Deck has 4 suits with 13 cards each")
    @Test
    void deckHasCorrectSuits() {
        Deck deck = new Deck();
        List<Card> cardList = deck.getCardList();

        // Set checks for dupes
        for (Suits s : Suits.values()) {
            Set<Card> suitSet = cardList.stream()
                .filter(c -> c.getSuit() == s)
                .collect(Collectors.toSet());

            assertEquals(13, suitSet.size());
        }
    }

    @DisplayName("Deck has 13 cards with a different suit each")
    @Test
    void deckHasCorrectCards() {
        Deck deck = new Deck();
        List<Card> cardList = deck.getCardList();

        // Set checks for dupes
        for (Ranks r : Ranks.values()) {
            Set<Card> rankSet = cardList.stream()
                .filter(c -> c.getRank() == r)
                .collect(Collectors.toSet());

            assertEquals(4, rankSet.size());
        }
    }

    @DisplayName("Deck can shuffle")
    @Test
    void deckCanShuffle() {
        Deck deck = new Deck();
        List<Card> initialCardList = new LinkedList<Card>(deck.getCardList());
        deck.shuffle();
        List<Card> shuffledCardList = deck.getCardList();
        assertNotEquals(initialCardList, shuffledCardList);
    }
}
