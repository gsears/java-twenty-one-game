package tech.hootlab;

import java.io.Serializable;

public class SocketMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String CONNECT = "CONNECT";
    public static final String SET_USER = "SET_USER";

    public static final String ADD_PLAYER = "ADD_PLAYER";
    public static final String REMOVE_PLAYER = "REMOVE_PLAYER";

    public static final String SET_USER_PLAYER = "SET_USER";
    public static final String SET_PLAYERS = "SET_PLAYERS";
    public static final String DISCONNECT = "DISCONNECT";

    public static final String HAND_UPDATE = "HAND_UPDATE";

    public static final String NEW_ROUND = "NEW_ROUND";
    public static final String PLAYER_CHANGE = "PLAYER_CHANGE";

    public static final String HIT = "HIT";
    public static final String STICK = "STICK";
    public static final String DEAL = "DEAL";


    private String message;
    private Serializable content;

    public SocketMessage(String message, Serializable content) {
        this.message = message;
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public Serializable getContent() {
        return content;
    }

}
