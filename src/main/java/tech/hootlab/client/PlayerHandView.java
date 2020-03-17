package tech.hootlab.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.logging.Logger;
import javax.swing.JLayeredPane;
import tech.hootlab.core.Card;
import tech.hootlab.core.Hand;

/**
 * A class which displays a hand on the player's 'table'.
 *
 * Inherits from JLayeredPane to allow cards to overlap. Because nice looking things get good marks,
 * right?
 */
public class PlayerHandView extends JLayeredPane {
    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(PlayerHandView.class.getName());

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
     * @param width  The width of the container
     * @param height
     */
    public PlayerHandView(int width, int height) {
        setOpaque(true);
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
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

    private void render() {
        // Repaint to make sure display is updated.
        repaint();
        // Revalidate as we've added new panels
        revalidate();
    }

    public void setHand(Hand hand) {
        LOGGER.info("Setting hand, with cards:" + hand.getCardList());
        clearCards();

        hand.getCardList().stream().forEach(card -> {
            addCard(card);
        });
    }

    private void addCard(Card card) {
        LOGGER.info("Adding card");
        CardView cardView = new CardView(card);
        int cardWidth = 2 * widthOffsetUnit;

        // This ensures an overlap
        cardView.setLocation(offset.x, offset.y);
        cardView.setSize(cardWidth, cardHeight);
        add(cardView, new Integer(cardDepth));

        render();

        // The next card overlaps on top
        cardDepth++;
        // Covers half of the previous card (as each card is 2*widthOffsetUnit)
        offset.x += widthOffsetUnit;
    }

    /**
     * Removes the cards in a player's hand.
     */
    private void clearCards() {
        LOGGER.info("Clearing cards");
        // Reset the offset.
        offset = new Point(widthOffsetUnit, heightOffsetUnit);
        removeAll();
        render();
    }
}
