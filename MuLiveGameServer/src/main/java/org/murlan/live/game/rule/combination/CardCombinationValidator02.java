package org.murlan.live.game.rule.combination;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;

import java.util.List;

/**
 * Checks for triple card combinations
 */
public class CardCombinationValidator02 implements ICardCombinationValidator {
    @Override
    public boolean isCardCombinationValid(CardCombination cardCombination) {
        List<Card> cards = cardCombination.getCards();
        if (cards.size() != 3) {
            return false;
        }

        boolean isTripleCards = cards.get(0).hasSameRankAs(cards.get(1))
                && cards.get(0).hasSameRankAs(cards.get(2))
                && cards.get(1).hasSameRankAs(cards.get(2));
        if (isTripleCards) {
            cardCombination.setType(CardCombinationType.TRIPLE_CARDS);
            return true;
        }
        return false;
    }
}
