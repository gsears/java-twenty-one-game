package tech.hootlab.client;

import javax.swing.SwingUtilities;

public class Client {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ClientController controller = new ClientController();
            ClientView gui = new ClientView(controller);
            gui.setVisible(true);
        });
    }
}
