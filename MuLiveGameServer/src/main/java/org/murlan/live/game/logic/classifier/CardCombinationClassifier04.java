package org.murlan.live.game.logic.classifier;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;
import org.murlan.live.game.deck.Suit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Checks for Kolor Bombs
 */
public class CardCombinationClassifier04 implements ICardCombinationClassifier {
    private final CardCombinationClassifier05 kolorValidator = new CardCombinationClassifier05();

    @Override
    public boolean classifyCardCombination(CardCombination cardCombination) {
        boolean isKolor = kolorValidator.classifyCardCombination(cardCombination);
        if (!isKolor) {
            return false;
        }

        List<Card> cards = cardCombination.getCards();
        Map<Suit, Long> cardCountBySuit = cards.stream().collect(Collectors.groupingBy(Card::suit, Collectors.counting()));
        boolean isBombKolor = cardCountBySuit.size() == 1;
        if (isBombKolor) {
            cardCombination.setType(CardCombinationType.BOMB_COLOR);
            return true;
        }
        return false;
    }
}
