package org.murlan.live.game.deck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CardCombination {
    private final List<Card> cards;
    private CardCombinationType type;

    public CardCombination(Card... cards) {
        this.cards = Arrays.asList(cards);
    }

    @Override
    public String toString() {
        String prefix = "";
        StringBuilder builder = new StringBuilder();
        for (Card card : cards) {
            builder.append(prefix).append(card.name());
            prefix = ", ";
        }
        return builder.toString();
    }
}
