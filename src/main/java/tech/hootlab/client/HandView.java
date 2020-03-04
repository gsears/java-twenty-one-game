package tech.hootlab.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import tech.hootlab.core.Ranks;
import tech.hootlab.core.Suits;

/**
 * A class which displays a hand on the player's 'table'.
 *
 * Inherits from JLayeredPane to allow cards to overlap. Because nice looking things get good marks,
 * right?
 */
public class HandView extends JLayeredPane {
    private static final long serialVersionUID = 1L;

    final private Color DARK_GREEN = new Color(0, 153, 0);

    private Point offset;
    private int widthOffsetUnit;
    private int heightOffsetUnit;
    private int cardHeight;
    private int cardDepth = 1;

    /**
     * Creates a container which can display a hand. The cards inside it will be automatically sized
     * so as to allow the maximum hand size.
     *
     * Defaults to 800x250, just because.
     */
    public HandView() {
        this(800, 250);
    }

    /**
     * Creates a container which can display a hand. The cards inside it will be automatically sized
     * so as to allow the maximum hand size.
     *
     * @param width  The width of the container
     * @param height
     */
    public HandView(int width, int height) {
        setOpaque(true);
        setPreferredSize(new Dimension(width, height));
        setBackground(DARK_GREEN);

        // Width - max number of cards in a hand is 11 (4*A, 4*2, 3*3).
        // Divide width by 14 (2 unit padding, 1 unit overlap, 2 unit visible card).
        widthOffsetUnit = width / 14;

        // Pad height 10%
        heightOffsetUnit = (int) (height * 0.1);
        cardHeight = height - (2 * heightOffsetUnit);

        // Cards are added according to this offset
        offset = new Point(widthOffsetUnit, heightOffsetUnit);

    }

    /**
     * Adds a card to the player's hand view.
     *
     * @param suit The card's suit.
     * @param rank The card's rank.
     */
    public void addCard(Suits suit, Ranks rank) {
        CardView card = new CardView(suit, rank);
        int cardWidth = 2 * widthOffsetUnit;

        // This ensures an overlap
        card.setLocation(offset.x, offset.y);
        card.setSize(cardWidth, cardHeight);
        add(card, new Integer(cardDepth));

        // Repaint to make sure display is updated.
        repaint();

        // The next card overlaps on top
        cardDepth++;
        // Covers half of the previous card (as each card is 2*widthOffsetUnit)
        offset.x += widthOffsetUnit;
    }

    /**
     * Removes the cards in a player's hand.
     */
    public void clearCards() {
        // Reset the offset.
        offset = new Point(widthOffsetUnit, heightOffsetUnit);
        removeAll();
        repaint();
    }


    public static void main(String[] args) {
        // Create and set up the window.
        JFrame frame = new JFrame("LayeredPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        HandView cardContainer = new HandView(800, 250);
        cardContainer.setOpaque(true); // content panes must be opaque
        frame.setContentPane(cardContainer);

        // Display the window.
        frame.pack();
        frame.setVisible(true);

        cardContainer.addCard(Suits.CLUBS, Ranks.TEN);
        cardContainer.addCard(Suits.DIAMONDS, Ranks.ACE);
        cardContainer.addCard(Suits.HEARTS, Ranks.ACE);
        cardContainer.clearCards();
        cardContainer.addCard(Suits.HEARTS, Ranks.ACE);
    }
}
