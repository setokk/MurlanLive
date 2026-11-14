package org.murlan.live.game.classifier;

import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;
import org.murlan.live.game.deck.Rank;
import org.murlan.live.game.deck.Suit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Checks for Kolors and Kolor Bombs
 */
public class CardCombinationClassifier04 implements ICardCombinationClassifier {
    @Override
    public boolean classifyCardCombination(CardCombination cardCombination) {
        if (!classifyKolor(cardCombination)) {
            return false;
        }

        List<Card> cards = cardCombination.getCards();
        Map<Suit, Long> cardCountBySuit = cards.stream().collect(Collectors.groupingBy(Card::suit, Collectors.counting()));
        boolean isBombKolor = cardCountBySuit.size() == 1;
        if (isBombKolor) {
            cardCombination.setType(CardCombinationType.BOMB_KOLOR);
        }

        return true;
    }

    public boolean classifyKolor(CardCombination cardCombination) {
        List<Card> cards = cardCombination.getCards();
        if (cards.size() < 5 || cards.contains(Card.BLACK_JOKER) || cards.contains(Card.RED_JOKER)) {
            return false;
        }

        for (int i = 1; i < cards.size(); i++) {
            Card card = cards.get(i);
            Card prevCard = cards.get(i - 1);
            int rankDiff = card.rank().ordinal() - prevCard.rank().ordinal();
            rankDiff = doesKolorStartFromAceOrTwo(cardCombination, card, rankDiff) ? 1 : rankDiff;
            if (rankDiff != 1) {
                return false;
            }
        }
        cardCombination.setType(CardCombinationType.KOLOR);
        return true;
    }

    /**
     * Helper method to check if a Kolor starts from Ace or Two. <br/>
     * The reason this method is needed is because:
     * <ul>
     *     <li>The way the Kolor classification is handled is checking if each current and previous card have Rank difference equal to 1</li>
     *     <li>Cards inside CardCombination are sorted on ASC order based on Rank (3->Red Joker)</li>
     *     <li>This means that Kolors like (ACE -> TWO -> THREE -> FOUR - FIVE) and (TWO -> THREE -> FOUR -> FIVE -> SIX cannot be classified</li>
     * </ul>
     * @param cardCombination the card combination played
     * @param card the current card that is being checked by the classifier
     * @param rankDiff the rank difference between the current and the previous card
     * @return {@code true}     if the Kolor is a valid one starting from Ace or Two <br/>
     *         {@code false}    if the Kolor is <b>not</b> a valid one starting from Ace or Two
     */
    private boolean doesKolorStartFromAceOrTwo(CardCombination cardCombination, Card card, int rankDiff) {
        if (rankDiff == 1) {
            return false;
        }
        if (card.rank().equals(Rank.ACE)) {
            return cardCombination.containsRank(Rank.THREE)
                    && cardCombination.containsRank(Rank.TWO)
                    && !cardCombination.containsRank(Rank.KING);
        } else if (card.rank().equals(Rank.TWO)) {
            return cardCombination.containsRank(Rank.THREE);
        }
        return false;
    }
}
