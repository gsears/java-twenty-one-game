package tech.hootlab.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import tech.hootlab.core.Player;

public class ClientView extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String QUIT_MESSAGE =
            "Hope you're back for mo' blackjack and twenty-one fun! Bye!";

    private static final int WINDOW_WIDTH = 800;
    private static final int PLAYER_SECTION_HEIGHT = 250;
    private static final int WINDOW_HEIGHT = 800;
    private static final int SCROLLBAR_WIDTH = 10;

    private JPanel gamePanel;
    private JScrollPane gameContainer;
    private Map<String, PlayerView> playerViewMap = new HashMap<>();

    private JPanel userPanel;
    private PlayerView userView;
    private PlayerControlView userControlView;

    public ClientView(ClientController controller) {

        // Create a panel for storing other players
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.PAGE_AXIS));

        gameContainer = new JScrollPane(gamePanel);
        gameContainer.getVerticalScrollBar().setPreferredSize(new Dimension(SCROLLBAR_WIDTH, 0));
        gameContainer.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        gameContainer.getViewport().setPreferredSize(
                new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - PLAYER_SECTION_HEIGHT));

        // Create a panel for storing user
        userPanel = new JPanel(new BorderLayout());
        Dimension userPanelDimension = new Dimension(WINDOW_WIDTH, PLAYER_SECTION_HEIGHT);
        userPanel.setMinimumSize(userPanelDimension);
        userPanel.setPreferredSize(userPanelDimension);

        // Create user view and control panel
        userView = new PlayerView(null, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT);
        userControlView = new PlayerControlView(controller);

        userPanel.add(userView, BorderLayout.CENTER);
        userPanel.add(userControlView, BorderLayout.SOUTH);

        // Divide other players' views from user view
        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameContainer, userPanel);

        // Handle quit programatically (for disconnecting users when they drop tokens)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                controller.disconnect(QUIT_MESSAGE);
            }
        });

        // Set the pane
        add(pane);
        // Compress to the correct size
        pack();
        // Place in a suitable area
        setLocationByPlatform(true);
    }

    private void render(JComponent component) {
        component.repaint();
        // Revalidate is needed, because we're adding JPanels on the fly.
        component.revalidate();
    }

    // Player view actions

    public void setUser(Player player) {
        userView.setPlayer(player);
        // Store for later referencing by player ID
        playerViewMap.put(player.getID(), userView);
        render(userPanel);
    }

    public void addPlayer(Player player) {
        PlayerView playerView =
                new PlayerView(player, WINDOW_WIDTH - SCROLLBAR_WIDTH, PLAYER_SECTION_HEIGHT);
        // Store for later referencing by player ID
        playerViewMap.put(player.getID(), playerView);
        gamePanel.add(playerView);
        render(gamePanel);
    }

    public void clearPlayers() {
        for (PlayerView playerView : playerViewMap.values()) {
            gamePanel.remove(playerView);
        }
        render(gamePanel);
    }

    public void updateHand(Player player) {

        PlayerView playerView = playerViewMap.get(player.getID());
        playerView.setHand(player.getHand());
    }

    public void updateTokens(Player player) {

        PlayerView playerView = playerViewMap.get(player.getID());
        playerView.setTokens(player.getTokens());
    }

    public void updateStatus(Player player) {

        PlayerView playerView = playerViewMap.get(player.getID());
        playerView.setStatus(player.getStatus());
    }

    // Round Actions

    public void setDealer(Player player) {
        String playerID = player.getID();

        Iterator<Entry<String, PlayerView>> iterator = playerViewMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, PlayerView> entry = iterator.next();
            PlayerView playerView = entry.getValue();

            if (entry.getKey().equals(playerID)) {
                playerView.setDealer(true);
            } else {
                playerView.setDealer(false);
            }
        }
    }

    public void setCurrentPlayer(Player player) {

        // If the current player doesn't exist, all players will be reset.
        String playerID = player == null ? null : player.getID();

        Iterator<Entry<String, PlayerView>> iterator = playerViewMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, PlayerView> entry = iterator.next();
            PlayerView playerView = entry.getValue();

            if (entry.getKey().equals(playerID)) {
                playerView.setCurrentPlayer(true);
            } else {
                playerView.setCurrentPlayer(false);
            }
        }
    }

    // Control Actions

    public void setDealerControl() {
        userControlView.enableDealButton();
    }

    public void setPlayerControl() {
        userControlView.enablePlayButtons();
    }

    public void disableControl() {
        userControlView.disableButtons();
    }

    // Message Actions

    public void displayMessage(String message) {
        userControlView.displayMessage(message);
    }

    public void clearMessage() {
        userControlView.clearMessage();
    }

}
