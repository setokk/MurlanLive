package org.murlan.live.game.deck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Shuffler {
    /**
     * Implementation of the Fisher Yates Shuffling algorithm
     * @param numParts the number of parts to divide the decks
     * @return the shuffled list of decks (size=<code>numParts</code>)
     *
     */
    public static List<Deck> shuffle(int numParts) {
        List<Card> cards = new ArrayList<>(Arrays.asList(Card.values())); // Deep copy to not mutate original values() array
        Random rand = new Random();
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Collections.swap(cards, i, j);
        }

        int blockSize = cards.size() / numParts;
        List<Deck> decks = new ArrayList<>();
        for (int i = 0; i < numParts; i++) {
            if (i == numParts - 1) {
                decks.add(new Deck(cards.subList(i, cards.size())));
            } else {
                decks.add(new Deck(cards.subList(i, i + blockSize)));
            }
        }
        return decks;
    }
}
