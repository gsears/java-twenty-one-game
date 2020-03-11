package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;
import tech.hootlab.core.Player;

public class PlayerView extends JPanel {
    private static final long serialVersionUID = 1L;

    private Player player;

    private PlayerInfoView playerInfoView;
    private PlayerHandView playerHandView;

    public PlayerView(Player player, int width, int height) {
        this.player = player;

        // Layout
        setLayout(new BorderLayout());
        playerHandView = new PlayerHandView(width, height);
        playerInfoView = new PlayerInfoView(player);

        add(playerInfoView, BorderLayout.NORTH);
        add(playerHandView, BorderLayout.CENTER);

        render();
    }

    public void setPlayer(Player player) {
        this.player = player;
        render();
    }

    public void setCurrentPlayer(boolean isCurrentPlayer) {
        playerInfoView.setCurrentPlayer(isCurrentPlayer);
        // Auto scroll to show the current player
        this.scrollRectToVisible(this.getBounds());
    }

    public void setDealer(boolean isDealer) {
        playerInfoView.setDealer(isDealer);
    }

    public void render() {
        if (player != null) {
            // Render info
            playerInfoView.render();

            // Render cards
            playerHandView.setHand(player.getHand());
        } else {

        }

    }
}
