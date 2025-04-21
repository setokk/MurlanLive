package org.murlan.live.game.logic.classifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;
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
    public void testCardCombination_BOMB() {
        CardCombination combination = new CardCombination(
                Card.KING_OF_HEARTS,
                Card.KING_OF_DIAMONDS,
                Card.KING_OF_CLUBS,
                Card.KING_OF_SPADES
        );
        LOGGER.debug("testCardCombination_BOMB() -> Card combination: {{}} should be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertTrue(isClassified);
        Assert.assertEquals(CardCombinationType.BOMB, combination.getType());
    }

    @Test
    public void testCardCombination_TRIPLE_SINGLE() {
        CardCombination combination = new CardCombination(
                Card.KING_OF_HEARTS,
                Card.KING_OF_DIAMONDS,
                Card.KING_OF_CLUBS,
                Card.QUEEN_OF_CLUBS
        );
        LOGGER.debug("testCardCombination_TRIPLE_SINGLE() -> Card combination: {{}} should NOT be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertFalse(isClassified);
    }

    @Test
    public void testCardCombination_KOLOR_6() {
        CardCombination combination = new CardCombination(
                Card.ACE_OF_HEARTS,
                Card.TWO_OF_SPADES,
                Card.THREE_OF_DIAMONDS,
                Card.FOUR_OF_DIAMONDS,
                Card.FIVE_OF_SPADES,
                Card.SIX_OF_HEARTS
        );
        LOGGER.debug("testCardCombination_KOLOR_6() -> Card combination: {{}} should be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertTrue(isClassified);
        Assert.assertEquals(CardCombinationType.KOLOR, combination.getType());
    }

    @Test
    public void testCardCombination_KOLOR_5() {
        CardCombination combination = new CardCombination(
                Card.SIX_OF_HEARTS, // out of order intentionally to check internal sorting of CardCombination
                Card.TWO_OF_SPADES,
                Card.THREE_OF_DIAMONDS,
                Card.FOUR_OF_DIAMONDS,
                Card.FIVE_OF_SPADES
        );
        LOGGER.debug("testCardCombination_KOLOR_5() -> Card combination: {{}} should be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertTrue(isClassified);
        Assert.assertEquals(CardCombinationType.KOLOR, combination.getType());
    }

    @Test
    public void testCardCombination_BOMB_KOLOR_5() {
        CardCombination combination = new CardCombination(
                Card.SIX_OF_HEARTS, // out of order intentionally to check internal sorting of CardCombination
                Card.SEVEN_OF_HEARTS,
                Card.EIGHT_OF_HEARTS,
                Card.NINE_OF_HEARTS,
                Card.TEN_OF_HEARTS
        );
        LOGGER.debug("testCardCombination_BOMB_KOLOR_5() -> Card combination: {{}} should be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertTrue(isClassified);
        Assert.assertEquals(CardCombinationType.BOMB_COLOR, combination.getType());
    }

    @Test
    public void testCardCombination_SINGLE_4() {
        CardCombination combination = new CardCombination(
                Card.TWO_OF_SPADES,
                Card.THREE_OF_DIAMONDS,
                Card.FOUR_OF_DIAMONDS,
                Card.FIVE_OF_SPADES
        );
        LOGGER.debug("testCardCombination_SINGLE_4() -> Card combination: {{}} should not be classified!", combination);
        boolean isClassified = runClassifiers(combination);
        Assert.assertFalse(isClassified);
    }

    private boolean runClassifiers(CardCombination combination) {
        for (ICardCombinationClassifier classifier : classifiers) {
            if (classifier.classifyCardCombination(combination)) {
                return true;
            }
        }
        return false;
    }
}
