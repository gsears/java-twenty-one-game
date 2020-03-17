package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerInfoView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int MINIMUM_HEIGHT = 20;
    private static final int HORIZONTAL_PADDING = 10;

    private String playerName;
    private JLabel playerNameLabel;

    private JLabel tokenCountLabel;
    private JLabel playerStateLabel;

    private boolean isDealer = false;
    private boolean stateSet = false;

    public PlayerInfoView(int width) {

        setPreferredSize(new Dimension(width, MINIMUM_HEIGHT));
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        playerNameLabel = new JLabel();
        tokenCountLabel = new JLabel();
        playerStateLabel = new JLabel();

        add(Box.createRigidArea(new Dimension(HORIZONTAL_PADDING, 0)));
        add(playerNameLabel);
        add(Box.createHorizontalGlue());
        add(playerStateLabel);
        add(Box.createHorizontalGlue());
        add(tokenCountLabel);
        add(Box.createRigidArea(new Dimension(HORIZONTAL_PADDING, 0)));
    }


    public void setPlayerName(String name) {
        playerName = name;
        setNameDisplay();
    }

    private void setNameDisplay() {
        String dealerStr = isDealer ? " (dealer)" : "";
        String nameDisplay = String.format("%s%s", playerName, dealerStr);
        playerNameLabel.setText(nameDisplay);
    }

    public void setPlayerTokens(int tokenCount) {
        tokenCountLabel.setText(String.format("Tokens: %d", tokenCount));
    }

    public void setDealer(boolean isDealer) {
        this.isDealer = isDealer;
        setNameDisplay();
    }

    public void setCurrentPlayer(boolean isCurrentPlayer) {
        // If no previous state (i.e. not a WINNER / LOSER)
        if (!stateSet) {
            String currentPlayerStr = isCurrentPlayer ? "\u2605 CURRENT PLAYER \u2605" : "";
            playerStateLabel.setText(currentPlayerStr);
            playerStateLabel.setForeground(Color.BLACK);
        }
    }

    public void setWinner() {
        playerStateLabel.setText("WINNER");
        playerStateLabel.setForeground(Color.GREEN);
        stateSet = true;
    }

    public void setLoser() {
        playerStateLabel.setText("LOSER");
        playerStateLabel.setForeground(Color.RED);
        stateSet = true;
    }

    public void resetStatus() {
        stateSet = false;
        playerStateLabel.setText("");
        playerStateLabel.setForeground(Color.BLACK);
    }
}
