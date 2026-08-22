package org.murlan.live.game;

import org.murlan.live.game.deck.CardCombination;

public final class GameConstants {
    public static final int MAX_PLAYERS = 4;
    public static final short MAX_TOTAL_SCORE_TO_WIN = 1000;
    public static final short SCORE_PENALTY_LEAVE_ROOM = -10;
    public static final short SCORE_REMAINING_PLAYERS_AFTER_LEAVE_ROOM = 1;
    public static final short SCORE_PENALTY_LOST_CONNECTION = 0;
    public static final short SCORE_REMAINING_PLAYERS_AFTER_LOST_CONNECTION = 0;
    public static final CardCombination EMPTY_CARD_COMBINATION = new CardCombination();
    public static final long TURN_DURATION_SECONDS = 45;
}
