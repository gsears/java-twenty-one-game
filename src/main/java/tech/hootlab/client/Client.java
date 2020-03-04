package tech.hootlab.client;

import javax.swing.SwingUtilities;

public class Client {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }
}
