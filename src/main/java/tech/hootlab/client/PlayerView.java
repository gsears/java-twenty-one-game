package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class PlayerView extends JPanel {
    private static final long serialVersionUID = 1L;

    JLabel tokenCountLabel;
    JLabel handValueLabel;
    String playerName;
    Color defaultBackground;

    public PlayerView(String playerName) {
        defaultBackground = getBackground();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new TitledBorder(playerName));

        tokenCountLabel = new JLabel();
        handValueLabel = new JLabel();

        // Initialise
        setDealer(false);
        setTokens(0);
        setHandValue(0);

        add(tokenCountLabel);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(handValueLabel);
    }

    public void setDealer(boolean isDealer) {
        String dealerStr = isDealer ? " (dealer)" : "";
        setBorder(new TitledBorder(String.format("%s%s", playerName, dealerStr)));
    }

    public void setTokens(int numTokens) {
        tokenCountLabel.setText(String.format("Tokens: %d", numTokens));
    }

    public void setHandValue(int handValue) {
        handValueLabel.setText(String.format("Hand Value: %d", handValue));
    }

    public void setWinner() {
        setBackground(Color.GREEN);
    }

    public void setLoser() {
        setBackground(Color.RED);
    }

    public void setPlaying() {
        setBackground(defaultBackground);
    }
}
