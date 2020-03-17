package tech.hootlab.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import tech.hootlab.core.Player;

public class ClientView extends JFrame {
    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(ClientView.class.getName());

    private static final int WINDOW_WIDTH = 800;
    private static final int PLAYER_SECTION_HEIGHT = 250;
    private static final int WINDOW_HEIGHT = 800;

    private JPanel gamePanel;
    private JScrollPane gameContainer;
    private Map<String, PlayerView> playerViewMap = new HashMap<>();

    private JPanel userPanel;
    private PlayerView userView;
    private PlayerControlView userControlView;

    public ClientView(ClientController controller) {
        // Listen for model property changes

        // model.addPropertyChangeListener(this);



        // Create a panel for storing other players
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.PAGE_AXIS));

        gameContainer = new JScrollPane(gamePanel);
        gameContainer.getViewport().setPreferredSize(
                new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - PLAYER_SECTION_HEIGHT));

        // Create a panel for storing user
        userPanel = new JPanel(new BorderLayout());
        userPanel.setMinimumSize(new Dimension(WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));

        userView = new PlayerView(null, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT);

        userControlView = new PlayerControlView(controller);
        userPanel.add(userView, BorderLayout.CENTER);
        userPanel.add(userControlView, BorderLayout.SOUTH);

        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameContainer, userPanel);

        // Handle quit programatically (for disconnecting users when they drop tokens)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                controller.quit();
            }
        });

        add(pane);
        pack();
        setLocationByPlatform(true);
    }

    public void setUser(Player player) {
        userView.setPlayer(player);
        playerViewMap.put(player.getID(), userView);
        userPanel.repaint();
        userPanel.revalidate();
    }

    public void addPlayer(Player player) {
        LOGGER.info("Adding player: " + player);
        PlayerView playerView = new PlayerView(player, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT);
        playerViewMap.put(player.getID(), playerView);
        gamePanel.add(playerView);
        gamePanel.repaint();
        gamePanel.validate();
    }

    public void clearPlayers() {
        for (PlayerView playerView : playerViewMap.values()) {
            gamePanel.remove(playerView);
        }
        gamePanel.repaint();
        gamePanel.revalidate();
    }

    public void updateHand(Player player) {
        LOGGER.info("Updating player hand: " + player);
        PlayerView playerView = playerViewMap.get(player.getID());
        playerView.setHand(player.getHand());
        gamePanel.repaint();
        gamePanel.revalidate();

    }

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

    public void setDealerControl() {
        userControlView.enableDealButton();
    }

    public void setPlayerControl() {
        userControlView.enablePlayButtons();
    }

    public void disableControl() {
        userControlView.disableButtons();
    }

    public void displayMessage(String message) {
        userControlView.displayMessage(message);
    }

    public void clearMessage() {
        userControlView.clearMessage();
    }

    public void setCurrentPlayer(Player player) {
        String playerID = player.getID();

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

    // @Override
    // public void propertyChange(PropertyChangeEvent evt) {
    // System.out.println("Property Change");
    // // Will always be a player
    // Object val = evt.getNewValue();
    // Player player = (Player) val;
    // LOGGER.info("Property Change Event: " + player);

    // switch (evt.getPropertyName()) {
    // case ClientModel.USER_CHANGE_EVENT:
    // LOGGER.info("User Change Event");
    // setUser(player);
    // break;

    // case ClientModel.PLAYER_ADD_EVENT:
    // addPlayer(player);
    // break;

    // case ClientModel.PLAYER_REMOVE_EVENT:
    // removePlayer(player);
    // break;

    // case ClientModel.DEALER_CHANGE_EVENT:
    // setDealer(player);

    // case ClientModel.CURRENT_PLAYER_CHANGE_EVENT:
    // setCurrentPlayer(player);

    // default:
    // break;
    // }

    // }



}

// otherPlayerContainer.add(new PlayerView(p1, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));
// otherPlayerContainer.add(new PlayerView(p2, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));
// otherPlayerContainer.add(new PlayerView(p3, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));
// PlayerView mary = new PlayerView(p4, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT);
// otherPlayerContainer.add(mary);

// Player p0 = new Player("Nathan", 300);
// p0.getHand().add(new Card(Suits.CLUBS, Ranks.TEN));

// Player p1 = new Player("Gareth", 400);
// p1.getHand().add(new Card(Suits.CLUBS, Ranks.ACE));
// Player p2 = new Player("Tim", 200);
// Player p3 = new Player("John", 200);
// Player p4 = new Player("Mary", 200);
// p4.getHand().add(new Card(Suits.DIAMONDS, Ranks.TEN));
// p4.getHand().add(new Card(Suits.CLUBS, Ranks.TWO));
