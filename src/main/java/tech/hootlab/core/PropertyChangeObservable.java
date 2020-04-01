package tech.hootlab.core;

import java.beans.PropertyChangeListener;

/*
 * PropertyChangeObservable.java
 *
 * Gareth Sears - 2493194S
 *
 * An interface which is used to guarantee an implementation of PropertyChangeSupport on the class.
 * Provides methods for adding / removing listeners.
 */
public interface PropertyChangeObservable {

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener pcl);

    public void addPropertyChangeListener(PropertyChangeListener pcl);

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener pcl);

    public void removePropertyChangeListener(PropertyChangeListener pcl);

}
