package tech.hootlab.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.UUID;

/*
 * Player.java
 *
 * Gareth Sears - 2493194S
 *
 * A class representing the state of a player, their hand, and their tokens.
 *
 * The class is observable using the Java Beans property change listener (thread safe), with static
 * properties containing the property change events. This allows the controller to easily keep track
 * of state changes. However, in multithread environments there is no guarantee of the order of
 * updates. That said, this shouldn't matter as far as user experience goes, as the end result will
 * be the same.
 *
 * The current implementation always fires update events with the old value as 'null'. This was for
 * ease in making the class thread-safe, as we want to fire event changes outside of syncrhonised
 * blocks to prevent potential deadlocks (i.e. if another thread tried accessing the object in the
 * callback function). This could potentially be improved going forward.
 *
 * Designed to be thread-safe, so can be passed around.
 */
public class Player implements PropertyChangeObservable, Serializable {
    private static final long serialVersionUID = 1L;

    private final String ID;
    private final String name;

    private int tokens;
    private Hand hand;
    private PlayerState status = PlayerState.PLAYING;

    // Uses empty object array, as this is serializable
    // https://stackoverflow.com/questions/15638972/is-it-okay-to-to-make-the-lock-transient-for-a-serializable-class
    private final Object tokenLock = new Object[0];
    private final Object handLock = new Object[0];
    private final Object statusLock = new Object[0];

    // Observable properties
    public static final String HAND_CHANGE_EVENT = "PLAYER_HAND_CHANGE";
    public static final String TOKEN_CHANGE_EVENT = "PLAYER_TOKEN_CHANGE";
    public static final String STATUS_CHANGE_EVENT = "PLAYER_STATUS_CHANGE";

    private PropertyChangeSupport propertyChangeSupport;

    public Player(String name, int initialTokens) {
        // Each player is identified by a unique ID
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

            // A helper class which embeds the transfer function.
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
            // the transferTokens function is called simultaneously by different threads, but with
            // the Player objects reversed (this -> target, and vice versa). Therefore, we need to
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

    // PropertyChangeObservable implementations
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
        // ID is immutable, so threadsafe comparisons.
        if (obj instanceof Player) {
            return ID.equals(((Player) obj).getID());
        } else if (obj instanceof String) {
            // Can compare if ID is the same
            return ID.equals((String) obj);
        } else {
            return false;
        }
    }
}
