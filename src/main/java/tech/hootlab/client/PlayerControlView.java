package tech.hootlab.client;

import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class PlayerControlView extends JPanel {
    private static final long serialVersionUID = 1L;

    JButton hitButton;
    JButton stickButton;

    /**
     * Creates a button panel which is used to control a player's turn.
     *
     * @param controller The controller.
     */
    PlayerControlView(ClientControllerInterface controller) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        add(Box.createHorizontalGlue());

        // Create hit button
        hitButton = new JButton("Hit");
        hitButton.addActionListener(e -> {
            controller.hit();
        });
        add(hitButton);

        add(Box.createRigidArea(new Dimension(10, 0)));

        // Create stick button
        stickButton = new JButton("Stick");
        stickButton.addActionListener(e -> {
            controller.stick();
        });
        add(stickButton);
    }

    /**
     * Enables or disables the button panel depending if it is the player's turn or not.
     *
     * @param b true to enable the buttons, false to disable them.
     */
    public void setButtonsEnabled(boolean b) {
        hitButton.setEnabled(b);
        stickButton.setEnabled(b);
    }

}
