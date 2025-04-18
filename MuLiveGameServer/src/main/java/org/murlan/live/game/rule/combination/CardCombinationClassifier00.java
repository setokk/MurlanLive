package org.murlan.live.game.rule.combination;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;

import java.util.List;

/**
 * Checks for single card combinations
 */
public class CardCombinationClassifier00 implements ICardCombinationClassifier {
    @Override
    public boolean classifyCardCombination(CardCombination cardCombination) {
        List<Card> cards = cardCombination.getCards();
        if (cards.size() != 1) {
            return false;
        }
        cardCombination.setType(CardCombinationType.SINGLE_CARD);
        return true;
    }
}
