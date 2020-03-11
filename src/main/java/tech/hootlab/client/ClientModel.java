package tech.hootlab.client;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import tech.hootlab.core.Player;

public class ClientModel {
    private final static Logger LOGGER = Logger.getLogger(ClientModel.class.getName());

    List<ModelListener> modelListeners = new LinkedList<>();

    List<Player> playerList = new LinkedList<>();
    Player user;
    Player currentPlayer;
    Player dealer;

    public void setUser(Player user) {
        LOGGER.info("User added: " + user);
        this.user = user;
    }

    public void addPlayer(Player player) {
        LOGGER.info("Player added: " + player);
        playerList.add(player);
    }

    public void removePlayer(Player player) {
        LOGGER.info("Player removed: " + player);
        playerList.remove(player);
    }

    public void setDealer(Player player) {
        LOGGER.info("Dealer set: " + player);
        dealer = player;
    }

    public void setCurrentPlayer(Player player) {
        LOGGER.info("Current player set: " + player);
    }
}
