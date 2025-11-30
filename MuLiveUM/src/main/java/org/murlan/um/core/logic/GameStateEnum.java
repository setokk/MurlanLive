package org.murlan.um.core.logic;

public enum GameStateEnum {
    WAITING,
    GIVING_CARDS,
    PLAYING,
    FINISHED;

    public static GameStateEnum fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal > values().length - 1) {
            throw new IllegalArgumentException("illegal ordinal: " + ordinal);
        }
        return values()[ordinal];
    }
}