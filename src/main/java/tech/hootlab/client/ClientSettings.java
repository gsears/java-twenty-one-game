package tech.hootlab.client;

import java.io.Serializable;

/*
 * ClientSettings.java
 *
 * Gareth Sears - 2493194S
 *
 * An object 'bean' which contains the player's settings. Essentially the clientside model. At this
 * stage this is immutable because it is set when the client connects.
 */
public class ClientSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int tokens;

    public ClientSettings(String name, int tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public String getName() {
        return name;
    }

    public int getTokens() {
        return tokens;
    }

}
