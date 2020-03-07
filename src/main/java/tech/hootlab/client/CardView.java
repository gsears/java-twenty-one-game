package tech.hootlab.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tech.hootlab.core.Card;
import tech.hootlab.core.CardRanks;
import tech.hootlab.core.CardSuits;

public class CardView extends JPanel {
    private static final long serialVersionUID = 1L;

    String SPADES_STRING = "\u2660";
    String CLUBS_STRING = "\u2663";
    String HEARTS_STRING = "\u2665";
    String DIAMONDS_STRING = "\u2666";

    String ACE_STRING = "A";
    String JACK_STRING = "J";
    String QUEEN_STRING = "Q";
    String KING_STRING = "K";

    Card card;

    CardView(Card card) {

        this.card = card;
        // setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.GRAY));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel smallLabelTopLeft = createCardLabel(16);
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(smallLabelTopLeft, c);

        JLabel centreLabel = createCardLabel(24);
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.CENTER;
        add(centreLabel, c);

        JLabel smallLabelBottomRight = createCardLabel(16);
        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        add(smallLabelBottomRight, c);

    }

    private JLabel createCardLabel(int fontSize) {

        CardSuits suit = card.getSuit();
        CardRanks rank = card.getRank();

        JLabel cardLabel = new JLabel();

        Color cardColor = getCardColor(suit);
        cardLabel.setForeground(cardColor);

        String cardText = getCardText(suit, rank);
        cardLabel.setText(cardText);

        cardLabel.setFont(new Font(cardLabel.getFont().getName(), Font.PLAIN, fontSize));

        return cardLabel;
    }

    private Color getCardColor(CardSuits suit) {

        switch (suit) {
            case SPADES:
                return Color.BLACK;

            case CLUBS:
                return Color.BLACK;

            case HEARTS:
                return Color.RED;

            case DIAMONDS:
                return Color.RED;

            default:
                throw new IllegalArgumentException("The supplied suit does not exist");
        }
    }

    private String getCardText(CardSuits suit, CardRanks rank) {

        String suitStr = "";
        String rankStr = "";

        switch (suit) {
            case SPADES:
                suitStr = SPADES_STRING;
                break;

            case CLUBS:
                suitStr = CLUBS_STRING;
                break;

            case HEARTS:
                suitStr = HEARTS_STRING;
                break;

            case DIAMONDS:
                suitStr = DIAMONDS_STRING;
                break;

            default:
                throw new IllegalArgumentException("The supplied suit does not exist");
        }

        switch (rank) {
            case ACE:
                rankStr = ACE_STRING;
                break;

            case JACK:
                rankStr = JACK_STRING;
                break;

            case QUEEN:
                rankStr = QUEEN_STRING;
                break;

            case KING:
                rankStr = KING_STRING;
                break;

            default:
                rankStr = Integer.toString(rank.getValue());
                break;
        }

        return String.format("%s%s", rankStr, suitStr);
    }

}
