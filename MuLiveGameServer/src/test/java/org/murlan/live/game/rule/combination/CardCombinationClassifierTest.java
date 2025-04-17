package org.murlan.live.game.rule.combination;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class CardCombinationClassifierTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardCombinationClassifierTest.class);

    private List<ICardCombinationClassifier> classifiers;

    @Before
    public void setUp() throws Exception {
        this.classifiers = Arrays.asList(
                new CardCombinationClassifier00(),
                new CardCombinationClassifier01(),
                new CardCombinationClassifier02(),
                new CardCombinationClassifier03(),
                new CardCombinationClassifier04(),
                new CardCombinationClassifier05()
        );
    }

    @Test
    public void testCardCombination_KH_KD_KC_KS() {
        CardCombination combination = new CardCombination(
                Card.KING_OF_HEARTS,
                Card.KING_OF_DIAMONDS,
                Card.KING_OF_CLUBS,
                Card.KING_OF_SPADES
        );
        LOGGER.debug("testCardCombination_KH_KD_KC_KS() -> Card combination: {{}} should be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertTrue(isClassified);
    }

    @Test
    public void testCardCombination_KH_KD_KC_QC() {
        CardCombination combination = new CardCombination(
                Card.KING_OF_HEARTS,
                Card.KING_OF_DIAMONDS,
                Card.KING_OF_CLUBS,
                Card.QUEEN_OF_CLUBS
        );
        LOGGER.debug("testCardCombination_KH_KD_KC_QC() -> Card combination: {{}} should NOT be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertFalse(isClassified);
    }

    private boolean runClassifiers(CardCombination combination) {
        for (ICardCombinationClassifier classifier : classifiers) {
            if (classifier.isCardCombinationClassified(combination)) {
                return true;
            }
        }
        return false;
    }
}
