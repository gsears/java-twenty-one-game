package tech.hootlab.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class ClientView extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int WINDOW_WIDTH = 800;
    private static final int PLAYER_SECTION_HEIGHT = 250;
    private static final int WINDOW_HEIGHT = 800;

    public ClientView(ClientModel model, ClientController controller) {

        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.PAGE_AXIS));

        JScrollPane gameContainer = new JScrollPane(gamePanel);
        gameContainer.getViewport().setPreferredSize(
                new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - PLAYER_SECTION_HEIGHT));

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setMinimumSize(new Dimension(WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));

        PlayerView userView = new PlayerView(null, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT);

        PlayerControlView playerHandButtonView = new PlayerControlView(controller);
        userPanel.add(userView, BorderLayout.CENTER);
        userPanel.add(playerHandButtonView, BorderLayout.SOUTH);

        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameContainer, userPanel);


        // HANDLE QUIT ELEGANTLY and POLITELY
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
