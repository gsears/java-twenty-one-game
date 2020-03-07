package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;

public class LoginView extends JPanel {
    private static final long serialVersionUID = 1L;

    JLabel nameLabel;
    JTextField nameField;
    JButton joinButton;

    public LoginView(ClientController controller) {

        setLayout(new GridBagLayout());

        nameLabel = new JLabel("Name: ");
        add(nameLabel);

        nameField = new JTextField("Your name", 20);
        add(nameField);

        joinButton = new JButton("Join Game");

        joinButton.addActionListener(e -> {
            controller.login(nameField.getText());
        });

        add(joinButton);
    }


}
