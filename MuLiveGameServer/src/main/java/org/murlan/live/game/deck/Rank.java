package org.murlan.live.game.deck;

public enum Rank {
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("T"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("1"),
    TWO("2"),
    BLACK_JOKER("BJ"),
    RED_JOKER("RJ");

    private final String id;

    Rank(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }
}
