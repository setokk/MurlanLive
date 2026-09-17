package org.murlan.live.game.deck;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShufflerTest {

    @Test
    public void testShuffle() {
        int numParts = 4;
        List<Hand> hands = Shuffler.shuffle(numParts);
        assertEquals(numParts, hands.size());
        for (Hand hand : hands) {
            System.out.println(hand.getCards().size());
        }
    }
}