package tech.hootlab.core;

public class Player {
    private String name;
    private int tokens;
    private Hand hand = new Hand(); // Initialise with empty hand.

    private PlayerState status = PlayerState.WAITING;

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

    public void clearHand() {
        hand = new Hand();
    }

    public PlayerState getStatus() {
        return status;
    }

    public void setStatus(PlayerState status) {
        this.status = status;
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
