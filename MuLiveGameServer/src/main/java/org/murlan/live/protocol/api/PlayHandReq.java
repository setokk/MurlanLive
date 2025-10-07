package org.murlan.live.protocol.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public final class PlayHandReq implements Req {
    private String JWT;
    private CardCombination cardCombination;

    public PlayHandReq(String[] messageParts, ProtocolConfig config) {
        if (messageParts.length != startIndex() + 1) {
            throw new InvalidDataException();
        }
        List<Card> cards = new ArrayList<>();
        String[] individualCards = messageParts[startIndex()].split(config.getProtocol_list_delimiter());
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
