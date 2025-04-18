package org.murlan.live.game.rule.combination;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks for Kolor Bombs
 */
public class CardCombinationClassifier05 implements ICardCombinationClassifier {
    private final CardCombinationClassifier04 kolorValidator = new CardCombinationClassifier04();

    @Override
    public boolean classifyCardCombination(CardCombination cardCombination) {
        boolean isKolor = kolorValidator.classifyCardCombination(cardCombination);
        if (!isKolor) {
            return false;
        }

        List<Card> cards = cardCombination.getCards();
        boolean isBombKolor = cards.stream()
                .collect(Collectors.groupingBy(Card::rank, Collectors.counting()))
                .size() == cards.size();
        if (isBombKolor) {
            cardCombination.setType(CardCombinationType.BOMB_COLOR);
            return true;
        }
        return false;
    }
}
