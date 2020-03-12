package tech.hootlab.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.SwingPropertyChangeSupport;
import tech.hootlab.core.Player;
import tech.hootlab.core.PropertyChangeObservable;

public class ClientModel implements PropertyChangeObservable {
    private final static Logger LOGGER = Logger.getLogger(ClientModel.class.getName());

    public static final String USER_CHANGE_EVENT = "USER_CHANGE";
    public static final String PLAYER_ADD_EVENT = "PLAYER_ADD";
    public static final String PLAYER_REMOVE_EVENT = "PLAYER_REMOVE";
    public static final String DEALER_CHANGE_EVENT = "DEALER_CHANGE";
    public static final String CURRENT_PLAYER_CHANGE_EVENT = "CURRENT_PLAYER_CHANGE";
    private PropertyChangeSupport propertyChangeSupport;

    private List<Player> playerList = new LinkedList<>();
    private Player user;
    private Player currentPlayer;
    private Player dealer;

    public ClientModel() {
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
    }

    public void setUser(Player user) {
        LOGGER.info("Old User " + this.user);
        LOGGER.info("User added: " + user);
        this.user = user;
        propertyChangeSupport.firePropertyChange(USER_CHANGE_EVENT, null, user);
    }

    public void addPlayer(Player player) {
        LOGGER.info("Player added: " + player);
        playerList.add(player);
        propertyChangeSupport.firePropertyChange(PLAYER_ADD_EVENT, null, player);
    }

    public synchronized void removePlayer(Player player) {
        LOGGER.info("Player removed: " + player);
        playerList.remove(player);
        propertyChangeSupport.firePropertyChange(PLAYER_REMOVE_EVENT, player, null);
    }

    public synchronized void setDealer(Player player) {
        LOGGER.info("Dealer set: " + player);
        propertyChangeSupport.firePropertyChange(DEALER_CHANGE_EVENT, dealer, player);
        dealer = player;

    }

    public synchronized void setCurrentPlayer(Player player) {
        LOGGER.info("Current player set: " + player);
        propertyChangeSupport.firePropertyChange(CURRENT_PLAYER_CHANGE_EVENT, currentPlayer,
                player);
        currentPlayer = player;
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, pcl);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, pcl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }
}
