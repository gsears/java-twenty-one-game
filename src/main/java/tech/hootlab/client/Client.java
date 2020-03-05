package tech.hootlab.client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import tech.hootlab.core.Hand;
import tech.hootlab.core.Player;

public class Client {

    public static void main(String[] args) {

        List<Player> playerList = new ArrayList<>();
        playerList.add(new Player("Gareth", 300));
        playerList.add(new Player("Tim", 500));

        Hand currentHand = new Hand();

        SwingUtilities.invokeLater(() -> {
            ClientControllerInterface controller = new ClientController();
            ClientView gui = new ClientView(controller);
            gui.setVisible(true);
        });
    }
}
