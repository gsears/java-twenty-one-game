package tech.hootlab.client;

import java.awt.BorderLayout;
import java.util.logging.Logger;
import javax.swing.JPanel;
import tech.hootlab.core.Hand;
import tech.hootlab.core.Player;
import tech.hootlab.core.PlayerState;

public class PlayerView extends JPanel {
    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(PlayerView.class.getName());

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
        // Auto scroll to show the current player
        if (isCurrentPlayer) {
            this.scrollRectToVisible(this.getBounds());
        }
    }

    public void setDealer(boolean isDealer) {
        playerInfoView.setDealer(isDealer);
    }

    public void setStatus(PlayerState status) {
        LOGGER.info("Setting status: " + status);
        switch (status) {
            case PLAYING:
                playerInfoView.resetStatus();
                break;

            case LOSER:
                LOGGER.info("Setting Loser!");
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

    // public void render() {
    // if (player != null) {
    // // Render info
    // playerInfoView.render();

    // // Render cards
    // playerHandView.setHand(player.getHand());
    // } else {

    // }

    // }
}
