package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import tech.hootlab.core.Player;

public class PlayerInfoView extends JPanel {

    private static final int HORIZONTAL_PADDING = 10;

    private JLabel playerNameLabel;
    private JLabel tokenCountLabel;
    private JLabel playerStateLabel;

    private boolean isCurrentPlayer = false;
    private boolean isDealer = false;
    private Player player;

    public PlayerInfoView(Player player) {
        this.player = player;

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

    public void render() {
        setName();
        setTokens();
        setState();
    }

    public void setDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    public void setCurrentPlayer(boolean isCurrentPlayer) {
        this.isCurrentPlayer = isCurrentPlayer;
    }

    private void setName() {

        String dealerStr = isDealer ? " (dealer)" : "";
        String nameDisplay = String.format("%s%s", player.getName(), dealerStr);

        playerNameLabel.setText(nameDisplay);
    }

    private void setTokens() {
        tokenCountLabel.setText(String.format("Tokens: %d", player.getTokens()));
    }

    private void setState() {
        switch (player.getStatus()) {
            case WAITING:
                String currentPlayerStr = isCurrentPlayer ? "\u2605 CURRENT PLAYER \u2605" : "";
                playerStateLabel.setText(currentPlayerStr);
                playerStateLabel.setForeground(Color.BLACK);
                break;

            case WINNER:
                playerStateLabel.setText("WINNER");
                playerStateLabel.setForeground(Color.YELLOW);
                break;

            case LOSER:
                playerStateLabel.setText("LOSER");
                playerStateLabel.setForeground(Color.RED);
                break;

            default:
                break;
        }
    }

}
