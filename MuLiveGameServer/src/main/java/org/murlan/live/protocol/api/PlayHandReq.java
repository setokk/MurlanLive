package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public final class PlayHandReq implements Req {
    private final CardCombination cardCombination;

    public PlayHandReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        if (messageParts.length != startIndex() + 1) {
            throw new InvalidDataException();
        }
        List<Card> cards = new ArrayList<>();
        String[] individualCards = messageParts[startIndex()].split(Pattern.quote(config.getProtocol_list_delimiter()));
        for (String individualCard : individualCards) {
            try {
                cards.add(Card.fromOrdinal(Integer.parseInt(individualCard)));
            } catch (IllegalArgumentException e) {
                break;
            }
        }
        cardCombination = new CardCombination(
                (cards.size() == individualCards.length) // Means no parsing error occurred (IllegalArgumentException)
                        ? cards
                        : Collections.emptyList()
        );
    }
}
