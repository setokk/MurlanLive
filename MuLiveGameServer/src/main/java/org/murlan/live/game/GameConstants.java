package org.murlan.live.game;

import org.murlan.live.game.deck.CardCombination;

public final class GameConstants {
    public static final int MAX_PLAYERS = 4;
    public static final short MAX_TOTAL_SCORE_TO_WIN = 30;
    public static final short SCORE_PENALTY_SURRENDER = -1;
    public static final short SCORE_REMAINING_PLAYERS = 1;
    public static final CardCombination EMPTY_CARD_COMBINATION = new CardCombination();
}
