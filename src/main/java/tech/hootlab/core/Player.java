package tech.hootlab.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

public class Player implements PropertyChangeObservable, Serializable {
    private static final long serialVersionUID = 1L;

    private final static Logger LOGGER = Logger.getLogger(Player.class.getName());

    private final String ID;
    private final String name;

    private int tokens;
    private final Object tokenLock = new Object();
    private Hand hand;
    private final Object handLock = new Object();
    private PlayerState status = PlayerState.PLAYING;
    private final Object statusLock = new Object();

    // Observable attributes
    // Class is observable to allow for easier tracking of state to transmit to
    // clients. The following are property change messages associated with the
    // events.
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
        synchronized (handLock) {
            return hand;
        }
    }

    public int getHandValue() {
        synchronized (handLock) {
            return hand.getValue();
        }
    }

    public void addCardToHand(Card card) {
        synchronized (handLock) {
            hand.add(card);
        }
        firePropertyChange(HAND_CHANGE_EVENT, null, hand.getCardList());

    }

    public synchronized void clearHand() {
        synchronized (handLock) {
            hand = new Hand();
        }
        firePropertyChange(HAND_CHANGE_EVENT, null, hand.getCardList());
    }

    public PlayerState getStatus() {
        synchronized (statusLock) {
            return status;
        }
    }

    public synchronized void setStatus(PlayerState status) {
        synchronized (statusLock) {
            this.status = status;
        }
        firePropertyChange(STATUS_CHANGE_EVENT, null, status);
    }

    public int getTokens() {
        synchronized (tokenLock) {
            return tokens;
        }
    }

    public void setTokens(int tokens) {
        synchronized (tokenLock) {
            this.tokens = tokens;
        }
    }

    public void transferTokens(Player target, int numTokens) {
        // Do nothing if the target player is the same as this player
        if (!target.equals(this)) {

            // A helper class which embeds the transfer function
            // This pattern is used in the Java Concurrency in Practice book

            class Helper {
                public void transfer() {
                    if (numTokens > tokens) {
                        // Transfers as many tokens as they can, rinse them out!
                        target.tokens += tokens;
                        tokens = 0;
                    } else {
                        target.tokens += numTokens;
                        tokens -= numTokens;
                    }
                }
            }

            // As we are using nested locks, we need to take into account the case where
            // the transferTokens function is called simultaneously, but with the Player
            // objects reversed (this -> target, and vice versa). Therefore, we need to
            // induce order on the locks. We do this by ordering the locks via the players'
            // immutable ID values.

            int lockOrderComparison = this.getID().compareTo(target.getID());
            if (lockOrderComparison < 0) {
                synchronized (this.tokenLock) {
                    synchronized (target.tokenLock) {
                        new Helper().transfer();
                    }
                }
            } else if (lockOrderComparison > 0) {
                synchronized (target.tokenLock) {
                    synchronized (this.tokenLock) {
                        new Helper().transfer();
                    }
                }
            }

            target.firePropertyChange(TOKEN_CHANGE_EVENT, null, target.tokens);
            firePropertyChange(TOKEN_CHANGE_EVENT, null, tokens);
        }


    }

    // So other player classes can access fire property change (for example, with
    // transfer tokens)
    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
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
        // ID is immutable, so threadsafe.
        if (obj instanceof Player) {
            return ID.equals(((Player) obj).getID());
        } else {
            return false;
        }

    }

}
