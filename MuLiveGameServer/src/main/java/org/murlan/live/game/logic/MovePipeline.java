package org.murlan.live.game.logic;

import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.logic.classifier.CardCombinationClassifier00;
import org.murlan.live.game.logic.classifier.CardCombinationClassifier01;
import org.murlan.live.game.logic.classifier.CardCombinationClassifier02;
import org.murlan.live.game.logic.classifier.CardCombinationClassifier03;
import org.murlan.live.game.logic.classifier.CardCombinationClassifier04;
import org.murlan.live.game.logic.classifier.CardCombinationClassifier05;
import org.murlan.live.game.logic.classifier.ICardCombinationClassifier;

import java.util.HashSet;

/**
 * Pipeline to be executed whenever a hand is attempted to be played
 */
public class MovePipeline {
    private static final ICardCombinationClassifier[] cardCombinationClassifiers = new ICardCombinationClassifier[] {
            new CardCombinationClassifier00(),
            new CardCombinationClassifier01(),
            new CardCombinationClassifier02(),
            new CardCombinationClassifier03(),
            new CardCombinationClassifier04(),
            new CardCombinationClassifier05()
    };

    public static boolean validate(CardCombination cardCombination) {
        if (containsDuplicates(cardCombination)) {
            return false;
        }
        for (ICardCombinationClassifier cardCombinationClassifier : cardCombinationClassifiers) {
            boolean isClassified = cardCombinationClassifier.classifyCardCombination(cardCombination);
            if (isClassified) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsDuplicates(CardCombination cardCombination) {
        int numCardsReceived = cardCombination.getCards().size();
        if (numCardsReceived == 1) {
            return false;
        } else {
            int numDistinctCards = new HashSet<>(cardCombination.getCards()).size();
            return numCardsReceived != numDistinctCards;
        }
    }
}
