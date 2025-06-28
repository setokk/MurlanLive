package org.murlan.live.game.deck;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class CardCombination {
    private final List<Card> cards;
    private CardCombinationType type;
    private static final Card.CardComparator ASC_COMPARATOR = new Card.CardComparator();

    public CardCombination(List<Card> cards) {
        this.cards = cards;
        this.cards.sort(ASC_COMPARATOR);
    }

    public CardCombination(Card... cards) {
        this.cards = Arrays.asList(cards);
        this.cards.sort(ASC_COMPARATOR);
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

    public boolean containsRank(Rank rank) {
        return cards.stream().anyMatch(card -> card.rank().equals(rank));
    }

    public boolean isStrongerThan(CardCombination other) {
        return false;
    }
}
