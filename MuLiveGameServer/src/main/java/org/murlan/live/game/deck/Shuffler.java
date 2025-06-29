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
        Collections.shuffle(cards, new Random());

        List<Deck> decks = new ArrayList<>(numParts);
        for (int i = 0; i <numParts; i++) {
            decks.add(new Deck());
        }

        for (int i = 0; i < cards.size(); i++) {
            Deck deck = decks.get(i % numParts);
            deck.addCard(cards.get(i));
        }
        return decks;
    }
}
