package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;

public class ClientView extends JFrame {
    private static final long serialVersionUID = 1L;

    public ClientView(ClientControllerInterface controller) {

        JPanel container = new JPanel(new BorderLayout());

        HandView handView = new HandView();
        HandButtonView handButtonView = new HandButtonView(controller);
        PlayerContainerView playerView = new PlayerContainerView();

        container.add(handView, BorderLayout.CENTER);
        container.add(handButtonView, BorderLayout.SOUTH);
        container.add(playerView, BorderLayout.EAST);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(container);
        pack();
        setLocationByPlatform(true);
    }

}
