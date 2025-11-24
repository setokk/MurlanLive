package org.murlan.live.game.classifier;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;

import java.util.List;

/**
 * Checks for quadruple card combinations (Bombs)
 */
public class CardCombinationClassifier03 implements ICardCombinationClassifier {
    @Override
    public boolean classifyCardCombination(CardCombination cardCombination) {
        List<Card> cards = cardCombination.getCards();
        if (cards.size() != 4) {
            return false;
        }

        boolean isBomb = cards.get(0).hasSameRankAs(cards.get(1))
                && cards.get(0).hasSameRankAs(cards.get(2))
                && cards.get(0).hasSameRankAs(cards.get(3))
                && cards.get(1).hasSameRankAs(cards.get(2))
                && cards.get(1).hasSameRankAs(cards.get(3))
                && cards.get(2).hasSameRankAs(cards.get(3));
        if (isBomb) {
            cardCombination.setType(CardCombinationType.BOMB);
            return true;
        }
        return false;
    }
}
