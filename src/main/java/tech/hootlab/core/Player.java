package tech.hootlab.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class Player implements PropertyChangeObservable, Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(Player.class.getName());

    private String ID;
    private String name;
    private int tokens;
    private Hand hand;
    private PlayerState status = PlayerState.WAITING;

    // Observable attributes
    public static final String HAND_CHANGE_EVENT = "PLAYER_HAND_CHANGE";
    public static final String TOKEN_CHANGE_EVENT = "PLAYER_TOKEN_CHANGE";
    public static final String STATUS_CHANGE_EVENT = "PLAYER_STATUS_CHANGE";
    private PropertyChangeSupport propertyChangeSupport;

    public Player(String name, int initialTokens) {
        this(UUID.randomUUID().toString(), name, initialTokens);
    }

    public Player(String ID, String name, int initialTokens) {
        this.ID = ID;
        this.name = name;
        this.tokens = initialTokens;
        this.hand = new Hand();
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getHandValue() {
        return hand.getValue();
    }

    public void addCardToHand(Card card) {
        List<Card> previousCards = new LinkedList<>(hand.getCardList());
        hand.add(card);

        LOGGER.info("Adding card: " + card);
        propertyChangeSupport.firePropertyChange(HAND_CHANGE_EVENT, previousCards,
                hand.getCardList());


        LOGGER.info(String.format("PropChangeSupport should have fired: %s, %s", previousCards,
                hand.getCardList()));
    }

    public void clearHand() {
        List<Card> previousCards = hand.getCardList();
        hand = new Hand();
        propertyChangeSupport.firePropertyChange(HAND_CHANGE_EVENT, previousCards,
                hand.getCardList());
    }

    public PlayerState getStatus() {
        return status;
    }

    public void setStatus(PlayerState status) {
        PlayerState previousStatus = status;
        this.status = status;
        propertyChangeSupport.firePropertyChange(STATUS_CHANGE_EVENT, previousStatus, status);
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void transferTokens(Player target, int numTokens) {
        int previousTokens = tokens;

        if (numTokens < tokens) {
            // Transfers as many tokens as they can, rinse them out!
            target.tokens += tokens;
            tokens = 0;
        } else {
            target.tokens += numTokens;
            tokens -= numTokens;
        }

        propertyChangeSupport.firePropertyChange(TOKEN_CHANGE_EVENT, previousTokens, tokens);
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            return ID.equals(((Player) obj).getID());
        } else {
            return false;
        }

    }

}
