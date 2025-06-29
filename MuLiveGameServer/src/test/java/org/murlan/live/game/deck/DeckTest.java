package org.murlan.live.game.deck;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class DeckTest extends TestCase {

    public void testContains() {
        List<Card> sameCards = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES);

        CardCombination cardCombination = new CardCombination(sameCards);
        Deck deck = new Deck();
        deck.addCards(sameCards);

        assertTrue(deck.contains(cardCombination));
    }

    public void testNotContains() {
        List<Card> cardsToBePlayed = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES);
        List<Card> actualOwnedCards = Arrays.asList(Card.FIVE_OF_HEARTS, Card.ACE_OF_CLUBS);

        CardCombination cardCombination = new CardCombination(cardsToBePlayed);
        Deck deck = new Deck();
        deck.addCards(actualOwnedCards);

        assertFalse(deck.contains(cardCombination));
    }

    public void testContains_2() {
        List<Card> cardsToBePlayed = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES);
        List<Card> actualOwnedCards = Arrays.asList(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES, Card.ACE_OF_CLUBS);

        CardCombination cardCombination = new CardCombination(cardsToBePlayed);
        Deck deck = new Deck();
        deck.addCards(actualOwnedCards);

        assertTrue(deck.contains(cardCombination));
    }
}