package tech.hootlab;

import java.io.Serializable;

public class SocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    // This value is a 'poison pill' used to close blocking queues. (See Java BlockingQueue docs).
    // I've been working on this so long, for sanity's sake I'm throwing in an easter egg to amuse
    // myself. In the real world this would be a sensible name, like "POISON".
    public static final String POISON = "https://www.youtube.com/watch?v=_mej5wS7viw";

    public static final String CONNECT = "CONNECT";
    public static final String SET_USER = "SET_USER";

    public static final String SET_PLAYERS = "SET_PLAYERS";
    public static final String DISCONNECT = "DISCONNECT";

    public static final String HAND_UPDATE = "HAND_UPDATE";
    public static final String TOKEN_UPDATE = "TOKEN_UPDATE";
    public static final String STATUS_UPDATE = "STATUS_UPDATE";

    public static final String ROUND_STARTED = "ROUND_STARTED";
    public static final String ROUND_PLAYER_CHANGE = "ROUND_PLAYER_CHANGE";
    public static final String ROUND_IN_PROGRESS = "ROUND_IN_PROGRESS";
    public static final String ROUND_FINISHED = "ROUND_FINISHED";

    public static final String HIT = "HIT";
    public static final String STICK = "STICK";
    public static final String DEAL = "DEAL";

    private final String command;
    private final Serializable payload;

    public SocketMessage(String command, Serializable payload) {
        this.command = command;
        this.payload = payload;
    }

    public String getCommand() {
        return command;
    }

    public Serializable getPayload() {
        return payload;
    }

}
