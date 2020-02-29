package tech.hootlab.round;

/**
 * An interface which allows RoundListeners to be added to the Round.
 */
public interface RoundObservable {

    public void addRoundEventListener(RoundEventListener RoundEventListener);

    public void removeRoundEventListener(RoundEventListener RoundEventListener);

}
