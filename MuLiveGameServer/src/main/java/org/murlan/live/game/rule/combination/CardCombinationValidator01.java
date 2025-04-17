package org.murlan.live.game.rule.combination;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;

import java.util.List;

/**
 * Checks for double card combinations
 */
public class CardCombinationValidator01 implements ICardCombinationValidator {
    @Override
    public boolean isCardCombinationValid(CardCombination cardCombination) {
        List<Card> cards = cardCombination.getCards();
        if (cards.size() != 2) {
            return false;
        }

        boolean isDoubleCards = cards.get(0).hasSameRankAs(cards.get(1));
        if (isDoubleCards) {
            cardCombination.setType(CardCombinationType.DOUBLE_CARDS);
            return true;
        }
        return false;
    }
}
