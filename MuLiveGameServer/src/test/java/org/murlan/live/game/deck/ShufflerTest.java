package org.murlan.live.game.deck;

import junit.framework.TestCase;

import java.util.List;

public class ShufflerTest extends TestCase {

    public void testShuffle() {
        int numParts = 4;
        List<Hand> hands = Shuffler.shuffle(numParts);
        assertEquals(numParts, hands.size());
        for (Hand hand : hands) {
            System.out.println(hand.getCards().size());
        }
    }
}