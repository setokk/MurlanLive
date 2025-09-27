package org.murlan.live.game.deck;

import io.jsonwebtoken.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class Deck {
    @Singular("withCard")
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        Assert.notNull(card, "card must not be null");
        Assert.notNull(cards, "cards must not be null");
        this.cards.add(card);
    }

    public void addCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public void removeCards(Card... cards) {
        Assert.notNull(cards, "cards must not be null");
        this.cards.removeAll(List.of(cards));
    }

    public void removeCards(List<Card> cards) {
        Assert.notNull(cards, "cards must not be null");
        this.cards.removeAll(cards);
    }

    public void removeCards(CardCombination cardCombination) {
        Assert.notNull(cardCombination, "cardCombination must not be null");
        Assert.notNull(cardCombination.getCards(), "cardCombination.cards must not be null");
        this.cards.removeAll(cardCombination.getCards());
    }

    public boolean contains(CardCombination cardCombination) {
        int matchingCards = 0;
        for (Card card : cardCombination.getCards()) {
            for (Card ownedCard : this.cards) {
                if (ownedCard.equals(card)) {
                    matchingCards++;
                    break;
                }
            }
        }
        return matchingCards == cardCombination.getCards().size();
    }
}
