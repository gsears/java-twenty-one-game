package tech.hootlab;

import java.io.Serializable;

public class SocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

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

    private String command;
    private Serializable payload;

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
