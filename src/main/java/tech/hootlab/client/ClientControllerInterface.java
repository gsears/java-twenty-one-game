package tech.hootlab.client;

/**
 * An interface for the client controller. Programming to an interface allows for flexibility down
 * the line should the controller need to be changed.
 */
public interface ClientControllerInterface {
    void hit();

    void stick();
}
