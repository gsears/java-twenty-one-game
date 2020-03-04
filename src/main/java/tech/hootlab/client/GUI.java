package tech.hootlab.client;

import java.awt.*;
import javax.swing.*;

public class GUI extends JFrame {

    public GUI() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 600));

        JButton hitButton = new JButton("Hit");
        JButton stickButton = new JButton("Stick");

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(hitButton);
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(stickButton);
        panel.add(buttonPane, BorderLayout.SOUTH);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        pack();
        setLocationByPlatform(true);
    }

}
