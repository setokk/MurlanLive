package org.murlan.live.game.deck;

import junit.framework.TestCase;

public class CardCombinationTest extends TestCase {

    public void testIsWeakerThan() {
        CardCombination weakerCombination = new CardCombination(Card.FIVE_OF_HEARTS, Card.FIVE_OF_SPADES, Card.FIVE_OF_DIAMONDS);
        CardCombination strongerCombination = new CardCombination(Card.NINE_OF_HEARTS, Card.NINE_OF_SPADES, Card.NINE_OF_CLUBS);
        weakerCombination.setType(CardCombinationType.TRIPLE_CARDS);
        strongerCombination.setType(CardCombinationType.TRIPLE_CARDS);

        assertTrue(weakerCombination.isWeakerThan(strongerCombination));
    }

    public void testIsWeakerThan_2() {
        CardCombination weakerCombination = new CardCombination(Card.FOUR_OF_CLUBS, Card.FIVE_OF_SPADES, Card.SIX_OF_DIAMONDS, Card.SEVEN_OF_HEARTS, Card.EIGHT_OF_HEARTS);
        CardCombination strongerCombination = new CardCombination(Card.SIX_OF_DIAMONDS, Card.SEVEN_OF_HEARTS, Card.EIGHT_OF_HEARTS, Card.NINE_OF_HEARTS, Card.TEN_OF_DIAMONDS);
        weakerCombination.setType(CardCombinationType.KOLOR);
        strongerCombination.setType(CardCombinationType.KOLOR);

        assertTrue(weakerCombination.isWeakerThan(strongerCombination));
    }

    public void testIsWeakerThan_3() {
        CardCombination kolorOfFive = new CardCombination(Card.FOUR_OF_CLUBS, Card.FIVE_OF_SPADES, Card.SIX_OF_DIAMONDS, Card.SEVEN_OF_HEARTS, Card.EIGHT_OF_HEARTS);
        CardCombination kolorOfSix = new CardCombination(Card.SIX_OF_DIAMONDS, Card.SEVEN_OF_HEARTS, Card.EIGHT_OF_HEARTS, Card.NINE_OF_HEARTS, Card.TEN_OF_DIAMONDS, Card.JACK_OF_DIAMONDS);
        kolorOfFive.setType(CardCombinationType.KOLOR);
        kolorOfSix.setType(CardCombinationType.KOLOR);

        assertFalse(kolorOfFive.isWeakerThan(kolorOfSix)); // Has to be the same number of cards (5 != 6). This means the one of 6 is not really stronger
        assertFalse(kolorOfSix.isWeakerThan(kolorOfFive));
    }

    public void testIsWeakerThan_4() {
        CardCombination weakerCombination = new CardCombination(Card.ACE_OF_CLUBS, Card.TWO_OF_HEARTS, Card.THREE_OF_SPADES, Card.FOUR_OF_SPADES, Card.FIVE_OF_DIAMONDS);
        CardCombination strongerCombination = new CardCombination(Card.TWO_OF_HEARTS, Card.THREE_OF_SPADES, Card.FOUR_OF_SPADES, Card.FIVE_OF_DIAMONDS, Card.SIX_OF_SPADES);
        weakerCombination.setType(CardCombinationType.KOLOR);
        strongerCombination.setType(CardCombinationType.KOLOR);

        assertFalse(weakerCombination.isWeakerThan(strongerCombination));
    }

    public void testIsWeakerThan_5() {
        CardCombination weakerCombination = new CardCombination(Card.ACE_OF_CLUBS, Card.TWO_OF_HEARTS, Card.THREE_OF_SPADES, Card.FOUR_OF_SPADES, Card.FIVE_OF_DIAMONDS);
        CardCombination strongerCombination = new CardCombination(Card.THREE_OF_SPADES, Card.FOUR_OF_SPADES, Card.FIVE_OF_DIAMONDS, Card.SIX_OF_SPADES, Card.SEVEN_OF_HEARTS);
        weakerCombination.setType(CardCombinationType.KOLOR);
        strongerCombination.setType(CardCombinationType.KOLOR);

        assertFalse(weakerCombination.isWeakerThan(strongerCombination));
    }

    public void testIsWeakerThan_6() {
        CardCombination weakerCombination = new CardCombination(Card.TWO_OF_HEARTS, Card.THREE_OF_SPADES, Card.FOUR_OF_SPADES, Card.FIVE_OF_DIAMONDS, Card.SIX_OF_DIAMONDS);
        CardCombination strongerCombination = new CardCombination(Card.THREE_OF_SPADES, Card.FOUR_OF_SPADES, Card.FIVE_OF_DIAMONDS, Card.SIX_OF_SPADES, Card.SEVEN_OF_HEARTS);
        weakerCombination.setType(CardCombinationType.KOLOR);
        strongerCombination.setType(CardCombinationType.KOLOR);

        assertFalse(weakerCombination.isWeakerThan(strongerCombination));
    }
}