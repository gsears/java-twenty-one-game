package tech.hootlab.core;

import java.beans.PropertyChangeListener;

/**
 * An interface which allows RoundListeners to be added to the Round.
 */
public interface PropertyChangeObservable {

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener pcl);

    public void addPropertyChangeListener(PropertyChangeListener pcl);

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener pcl);

    public void removePropertyChangeListener(PropertyChangeListener pcl);

}
