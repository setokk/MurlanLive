package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.game.deck.Card;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class GiveCardReq implements Req {
    private final Card card;
    private final long receivingPlayerId;

    public GiveCardReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        this.card = Card.fromOrdinal(Integer.parseInt(messageParts[startIndex()]));
        this.receivingPlayerId = Long.parseLong(messageParts[startIndex() + 1]);
    }
}
