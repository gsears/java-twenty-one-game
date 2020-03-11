package tech.hootlab.client;

import java.io.Serializable;

public class ClientSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    int tokens = 0;

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
