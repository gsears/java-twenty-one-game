package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;
import tech.hootlab.core.Card;
import tech.hootlab.core.Player;
import tech.hootlab.core.Ranks;
import tech.hootlab.core.Suits;

public class ClientView extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int WINDOW_WIDTH = 1000;
    private static final int PLAYER_SECTION_HEIGHT = 250;
    private static final int WINDOW_HEIGHT = 600;

    public ClientView(ClientControllerInterface controller) {

        JPanel otherPlayerContainer = new JPanel();
        otherPlayerContainer.setLayout(new BoxLayout(otherPlayerContainer, BoxLayout.PAGE_AXIS));

        Player p1 = new Player("Gareth", 400);
        p1.getHand().add(new Card(Suits.CLUBS, Ranks.ACE));
        Player p2 = new Player("Tim", 200);
        Player p3 = new Player("John", 200);
        Player p4 = new Player("Mary", 200);
        p4.getHand().add(new Card(Suits.DIAMONDS, Ranks.TEN));
        p4.getHand().add(new Card(Suits.CLUBS, Ranks.TWO));

        otherPlayerContainer.add(new PlayerView(p1, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));
        otherPlayerContainer.add(new PlayerView(p2, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));
        otherPlayerContainer.add(new PlayerView(p3, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));
        PlayerView mary = new PlayerView(p4, WINDOW_WIDTH, PLAYER_SECTION_HEIGHT);
        otherPlayerContainer.add(mary);

        JScrollPane otherPlayers = new JScrollPane(otherPlayerContainer);

        otherPlayers.getViewport().setPreferredSize(
                new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT - PLAYER_SECTION_HEIGHT));

        PlayerHandView playerHandView = new PlayerHandView();
        PlayerControlView playerHandButtonView = new PlayerControlView(controller);

        JPanel playerArea = new JPanel(new BorderLayout());
        playerArea.setPreferredSize(new Dimension(WINDOW_WIDTH, PLAYER_SECTION_HEIGHT));
        playerArea.add(playerHandView, BorderLayout.CENTER);
        playerArea.add(playerHandButtonView, BorderLayout.SOUTH);


        JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, otherPlayers, playerArea);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(pane);
        pack();
        setLocationByPlatform(true);


    }

}
