package tech.hootlab.client;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * PlayerControlView.java
 *
 * Gareth Sears - 2493194S
 *
 * This is a panel which contains the message display as well as buttons for user input.
 */
public class PlayerControlView extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int HORIZONTAL_PADDING = 10;

    private JLabel messageDisplay;
    private JButton hitButton;
    private JButton stickButton;
    private JButton dealButton;

    /**
     * Creates a button panel which is used to control a player's turn.
     *
     * Delegates action responses to the controller.
     *
     * @param controller The controller.
     */
    PlayerControlView(ClientController controller) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Initial padding
        add(Box.createRigidArea(new Dimension(HORIZONTAL_PADDING, 0)));

        // Message display
        messageDisplay = new JLabel();
        add(messageDisplay);

        // Padding between buttons
        add(Box.createHorizontalGlue());

        // Hit Button
        hitButton = createButton("Hit", e -> {
            controller.hit();
        });

        // Stick button
        stickButton = createButton("Stick", e -> {
            controller.stick();
        });

        // Deal button
        dealButton = createButton("Deal", e -> {
            controller.deal();
        });

        clearMessage();
        disableButtons();
    }

    public void displayMessage(String message) {
        messageDisplay.setText(message);
    }

    public void clearMessage() {
        messageDisplay.setText("");
    }

    public void disableButtons() {
        hitButton.setEnabled(false);
        stickButton.setEnabled(false);
        dealButton.setEnabled(false);
    }

    public void enableDealButton() {
        hitButton.setEnabled(false);
        stickButton.setEnabled(false);
        dealButton.setEnabled(true);
    }

    public void enablePlayButtons() {
        hitButton.setEnabled(true);
        stickButton.setEnabled(true);
        dealButton.setEnabled(false);
    }

    private JButton createButton(String name, ActionListener listener) {
        // Create stick button
        JButton button = new JButton(name);
        button.addActionListener(listener);
        add(button);
        add(Box.createRigidArea(new Dimension(HORIZONTAL_PADDING, 0)));
        return button;
    }
}
