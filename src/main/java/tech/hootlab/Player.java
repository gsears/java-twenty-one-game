package tech.hootlab;

public class Player {
    private String name;
    private int tokens; // The money / chips / whatever...
    private Hand hand = new Hand();

    public Player(String name, int initialTokens) {
        this.name = name;
        this.tokens = initialTokens;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getTokens() {
        return tokens;
    }

    public void transferTokens(Player target, int numTokens) {
        if (numTokens < tokens) {
            // Transfers as many tokens as they can, rinse them out!
            target.tokens += tokens;
            tokens = 0;
        } else {
            target.tokens += numTokens;
            tokens -= numTokens;
        }
    }


}
