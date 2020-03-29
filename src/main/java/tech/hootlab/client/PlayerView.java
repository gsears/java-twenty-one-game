package tech.hootlab.client;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import tech.hootlab.core.Hand;
import tech.hootlab.core.Player;
import tech.hootlab.core.PlayerState;

/*
 * PlayerView.java
 *
 * Gareth Sears - 2493194S
 *
 * A JPanel which composes the PlayerHandView and the PlayerInfoView to a reusable component which
 * displays all required player state.
 */
public class PlayerView extends JPanel {
    private static final long serialVersionUID = 1L;

    private PlayerInfoView playerInfoView;
    private PlayerHandView playerHandView;

    public PlayerView(Player player, int width, int height) {

        // Layout
        setLayout(new BorderLayout());
        playerHandView = new PlayerHandView(width, height);
        playerInfoView = new PlayerInfoView(width);

        add(playerInfoView, BorderLayout.NORTH);
        add(playerHandView, BorderLayout.CENTER);

        setPlayer(player);
    }

    public void setPlayer(Player player) {
        if (player != null) {
            playerInfoView.setPlayerName(player.getName());
            setTokens(player.getTokens());
            setHand(player.getHand());
        }
    }

    public void setHand(Hand hand) {
        playerHandView.setHand(hand);
    }

    public void setTokens(int tokens) {
        playerInfoView.setPlayerTokens(tokens);
    }

    public void setCurrentPlayer(boolean isCurrentPlayer) {
        playerInfoView.setCurrentPlayer(isCurrentPlayer);
        // Auto scroll to show the current player if inside a scroll panel.
        // Who says you can't have UX in swing?
        if (isCurrentPlayer) {
            this.scrollRectToVisible(this.getBounds());
        }
    }

    public void setDealer(boolean isDealer) {
        playerInfoView.setDealer(isDealer);
    }

    public void setStatus(PlayerState status) {
        switch (status) {
            case PLAYING:
                playerInfoView.resetStatus();
                break;

            case LOSER:
                playerInfoView.setLoser();
                break;

            case WINNER:
                playerInfoView.setWinner();
                break;

            default:
                playerInfoView.resetStatus();
                break;
        }
    }
}
