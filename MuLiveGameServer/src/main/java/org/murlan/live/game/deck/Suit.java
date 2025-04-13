package org.murlan.live.game.deck;

public enum Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES, NONE;

    private final String id;

    Suit() {
        this.id = name().substring(0, 1); // First letter is the identifier
    }

    public String id() {
        if (this.equals(Suit.NONE)) {
            return "";
        }
        return id;
    }
}
