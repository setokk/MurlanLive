package org.murlan.live.game.logic;

import org.junit.jupiter.api.Test;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class MovePipelineTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovePipelineTest.class);

    @Test
    public void testDoubleCards() {
        CardCombination cardCombination = new CardCombination(Card.KING_OF_HEARTS, Card.KING_OF_DIAMONDS);
        LOGGER.debug("testDoubleCards() -> Card combination: {{}} should be valid!", cardCombination);
        boolean isMoveValid = MovePipeline.validate(cardCombination);
        assertTrue(isMoveValid);
    }

    @Test
    public void testDuplicateCards() {
        CardCombination cardCombination = new CardCombination(Card.QUEEN_OF_HEARTS, Card.QUEEN_OF_HEARTS);
        LOGGER.debug("testDuplicateCards() -> Card combination: {{}} should NOT be valid!", cardCombination);
        boolean isMoveValid = MovePipeline.validate(cardCombination);
        assertFalse(isMoveValid);
    }
}