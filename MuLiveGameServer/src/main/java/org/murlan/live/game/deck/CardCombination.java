package org.murlan.live.game.deck;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.murlan.live.game.deck.CardCombinationType.*;

@Getter
@Setter
public class CardCombination {
    private final List<Card> cards;
    private List<Card> kolorSortCards;
    private CardCombinationType type;
    private static final Card.CardComparator ASC_COMPARATOR = new Card.CardComparator();
    private static final Card.KolorComparator KOLOR_COMPARATOR = new Card.KolorComparator();

    public CardCombination(List<Card> cards) {
        this.cards = cards;
        this.cards.sort(ASC_COMPARATOR);
    }

    public CardCombination(Card... cards) {
        this.cards = Arrays.asList(cards);
        this.cards.sort(ASC_COMPARATOR);
    }

    public void setType(CardCombinationType type) {
        this.type = type;
        if (this.type.equals(KOLOR) || this.type.equals(BOMB_KOLOR)) {
            this.kolorSortCards = new ArrayList<>(this.cards);
            this.kolorSortCards.sort(KOLOR_COMPARATOR);
        }
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

    public Card getLowestCardOfKolor() {
        boolean isNotKolor = !this.getType().equals(KOLOR) && !this.getType().equals(BOMB_KOLOR);
        if (isNotKolor) {
            throw new IllegalStateException("Card combination is not of type Kolor");
        }
        if (kolorSortCards == null || kolorSortCards.isEmpty()) {
            throw new IllegalStateException("kolorSortCards is not properly initialized");
        }
        return kolorSortCards.getFirst();
    }

    public boolean isWeakerThan(CardCombination other) {
        return switch (this.type) {
            case SINGLE_CARD -> {
                boolean strongerBySingleCard = other.getType().equals(SINGLE_CARD)
                        && other.getCards().getFirst().hasBiggerRankThan(this.getCards().getFirst());
                boolean strongerByBombs = other.getType().equals(BOMB) || other.getType().equals(BOMB_KOLOR);
                yield strongerBySingleCard || strongerByBombs;
            }
            case DOUBLE_CARDS -> {
                boolean strongerByDoubleCards = other.getType().equals(DOUBLE_CARDS)
                        && other.getCards().getFirst().hasBiggerRankThan(this.getCards().getFirst());
                boolean strongerByBombs = other.getType().equals(BOMB) || other.getType().equals(BOMB_KOLOR);
                yield strongerByDoubleCards || strongerByBombs;
            }
            case TRIPLE_CARDS -> {
                boolean strongerTripleCards = other.getType().equals(TRIPLE_CARDS)
                        && other.getCards().getFirst().hasBiggerRankThan(this.getCards().getFirst());
                boolean strongerByBombs = other.getType().equals(BOMB) || other.getType().equals(BOMB_KOLOR);
                yield strongerTripleCards || strongerByBombs;
            }
            case BOMB -> {
                boolean strongerByBomb = other.getType().equals(BOMB) && other.getCards().getFirst().hasBiggerRankThan(this.getCards().getFirst());
                boolean strongerByBombKolor = other.getType().equals(BOMB_KOLOR);
                yield strongerByBomb || strongerByBombKolor;
            }
            case KOLOR -> {
                boolean strongerByKolor = other.getType().equals(KOLOR)
                        && other.getCards().size() == this.getCards().size()
                        && other.getLowestCardOfKolor().hasBiggerRankForKolorThan(this.getLowestCardOfKolor());
                boolean strongerByBombs = other.getType().equals(BOMB) || other.getType().equals(BOMB_KOLOR);
                yield strongerByKolor || strongerByBombs;
            }
            case BOMB_KOLOR -> other.getType().equals(BOMB_KOLOR)
                    && other.getCards().size() == this.getCards().size()
                    && other.getLowestCardOfKolor().hasBiggerRankForKolorThan(this.getLowestCardOfKolor());
        };
    }

    public boolean isStrongerThan(CardCombination other) {
        return !isWeakerThan(other);
    }
}
