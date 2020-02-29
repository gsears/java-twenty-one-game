package tech.hootlab;

public class Player {
    String name;
    int bank; // The money / chips / whatever...
    Hand hand = new Hand();

    public Player(String name, int initialBank) {
        this.name = name;
        this.bank = initialBank;
    }

    public Hand getHand() {
        return hand;
    }
}
