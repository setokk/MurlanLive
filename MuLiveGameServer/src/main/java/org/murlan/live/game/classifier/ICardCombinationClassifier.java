package org.murlan.live.game.classifier;

import org.murlan.live.game.deck.CardCombination;

/**
 * Interface that acts as a classifier for card combinations. <br/>
 */
public interface ICardCombinationClassifier {
    /**
     *
     * @param cardCombination the combination of cards a player has played (cards are assumed to have been sorted by rank in ASC order)
     * @return {@code true}     if the card combination has been identified <br/>
     *         {@code false}    if the card combination does not fall under this rule (does not mean it is invalid as a whole)
     */
    boolean classifyCardCombination(CardCombination cardCombination);
}
