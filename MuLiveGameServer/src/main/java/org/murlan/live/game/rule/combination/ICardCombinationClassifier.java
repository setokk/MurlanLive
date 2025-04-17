package org.murlan.live.game.rule.combination;

import org.murlan.live.game.deck.CardCombination;

/**
 * Interface that acts as a validator for card combinations. <br/>
 */
public interface ICardCombinationClassifier {
    /**
     *
     * @param cardCombination the combination of cards a player has played (cards are assumed to have been sorted by rank in ASC order)
     * @return {@code true}     if the card combination is valid <br/>
     *         {@code false}    if the card combination does not fall under this rule (does not mean it is invalid as a whole)
     */
    boolean isCardCombinationClassified(CardCombination cardCombination);
}
