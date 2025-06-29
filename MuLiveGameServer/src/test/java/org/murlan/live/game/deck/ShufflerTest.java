package org.murlan.live.game.deck;

import junit.framework.TestCase;

import java.util.List;

public class ShufflerTest extends TestCase {

    public void testShuffle() {
        int numParts = 4;
        List<Deck> decks = Shuffler.shuffle(numParts);
        assertEquals(numParts, decks.size());
        for (Deck deck : decks) {
            System.out.println(deck.getCards().size());
        }
    }
}