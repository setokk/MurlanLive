package org.murlan.live.game.rule.combination;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks for simple Kolors
 */
public class CardCombinationClassifier05 implements ICardCombinationClassifier {
    @Override
    public boolean classifyCardCombination(CardCombination cardCombination) {
        List<Card> cards = cardCombination.getCards();
        if (cards.size() < 5 || cards.contains(Card.BLACK_JOKER) || cards.contains(Card.RED_JOKER)) {
            return false;
        }

        for (int i = 1; i < cards.size(); i++) {
            Card card = cards.get(i);
            Card prevCard = cards.get(i - 1);
            if (card.rank().ordinal() - prevCard.rank().ordinal() != 1) {
                return false;
            }
        }
        cardCombination.setType(CardCombinationType.KOLOR);
        return true;
    }
}
