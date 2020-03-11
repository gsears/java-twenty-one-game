package tech.hootlab.server;

import java.io.Serializable;

public class ClientServerMessage implements Serializable {
    private static final long serialVersionUID = 1L;


    public static final String ADD_PLAYER = "ADD_PLAYER";
    public static final String REMOVE_PLAYER = "REMOVE_PLAYER";

    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";

    public static final String HIT = "HIT";
    public static final String STICK = "STICK";
    public static final String DEAL = "DEAL";


    private String message;
    private Serializable content;

    public ClientServerMessage(String message, Serializable content) {
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
