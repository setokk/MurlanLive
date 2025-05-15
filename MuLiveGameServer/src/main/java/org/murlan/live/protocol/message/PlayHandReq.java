package org.murlan.live.protocol.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PlayHandReq implements Req {
    private String JWT;
    private CardCombination cardCombination;

    public PlayHandReq(String[] messageParts, ProtocolConfig config) {
        List<Card> cards = new ArrayList<>();
        String[] individualCards = messageParts[2].split(config.getProtocol_card_delimiter());
        for (String individualCard : individualCards) {
            try {
                cards.add(Card.fromOrdinal(Integer.parseInt(individualCard)));
            } catch (IllegalArgumentException e) {
                break;
            }
        }
        cardCombination = new CardCombination(
                (cards.size() == individualCards.length) // Means no parsing error occurred
                        ? cards
                        : Collections.emptyList()
        );
    }
}
