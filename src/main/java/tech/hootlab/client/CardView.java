package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import tech.hootlab.Ranks;
import tech.hootlab.Suits;

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


    String cardText;

    CardView(Suits suit, Ranks rank) {
        setPreferredSize(new Dimension(80, 130));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEtchedBorder(Color.BLACK, Color.GRAY));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // TODO: Encapsulate this copy paste code

        JLabel cardText = new JLabel();
        cardText.setText(getCardText(suit, rank));
        cardText.setForeground(getCardColor(suit));

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(cardText, c);

        cardText = new JLabel();
        cardText.setText(getCardText(suit, rank));
        cardText.setForeground(getCardColor(suit));
        cardText.setFont(new Font(cardText.getFont().getName(), Font.PLAIN, 24));

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.CENTER;
        add(cardText, c);

        cardText = new JLabel();
        cardText.setText(getCardText(suit, rank));
        cardText.setForeground(getCardColor(suit));

        c.fill = GridBagConstraints.NONE;
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        add(cardText, c);

    }

    private Color getCardColor(Suits suit) {
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

    private String getCardText(Suits suit, Ranks rank) {
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

    public static void main(String[] args) {
        // Create and set up the window.
        JFrame frame = new JFrame("LayeredPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        JComponent newContentPane = new CardView(Suits.HEARTS, Ranks.TEN);
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
