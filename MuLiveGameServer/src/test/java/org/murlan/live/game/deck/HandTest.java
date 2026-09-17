package org.murlan.live.game.deck;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HandTest {

    @Test
    public void testContains() {
        List<Card> sameCards = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES);

        CardCombination cardCombination = new CardCombination(sameCards);
        Hand hand = new Hand();
        hand.addCards(sameCards);

        assertTrue(hand.contains(cardCombination));
    }

    @Test
    public void testNotContains() {
        List<Card> cardsToBePlayed = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES);
        List<Card> actualOwnedCards = Arrays.asList(Card.FIVE_OF_HEARTS, Card.ACE_OF_CLUBS);

        CardCombination cardCombination = new CardCombination(cardsToBePlayed);
        Hand hand = new Hand();
        hand.addCards(actualOwnedCards);

        assertFalse(hand.contains(cardCombination));
    }

    @Test
    public void testContains_2() {
        List<Card> cardsToBePlayed = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES);
        List<Card> actualOwnedCards = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES, Card.ACE_OF_CLUBS);

        CardCombination cardCombination = new CardCombination(cardsToBePlayed);
        Hand hand = new Hand();
        hand.addCards(actualOwnedCards);

        assertTrue(hand.contains(cardCombination));
    }
}