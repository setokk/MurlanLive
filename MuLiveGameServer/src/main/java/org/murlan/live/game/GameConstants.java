package org.murlan.live.game;

import org.murlan.live.game.deck.CardCombination;

import java.util.List;

public final class GameConstants {
    public static final int MAX_PLAYERS = 4;
    public static final short MAX_TOTAL_SCORE_TO_WIN = 50;
    public static final short SCORE_PENALTY_LEAVE_ROOM = -150;
    public static final short SCORE_PENALTY_LOST_CONNECTION = -5;
    public static final long RECONNECTION_GRACE_PERIOD_SECONDS = 120;
    public static final CardCombination EMPTY_CARD_COMBINATION = new CardCombination();
    public static final List<Long> TURN_DURATION_SECONDS_VALUES = List.of(30L, 45L, 60L, 75L, 90L, 105L, 120L);
    public static final int CHAT_CHARACTER_LIMIT = 256;
}
