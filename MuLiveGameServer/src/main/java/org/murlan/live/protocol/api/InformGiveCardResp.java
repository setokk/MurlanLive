package org.murlan.live.protocol.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.murlan.live.game.deck.Card;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.ServerEvent;
import org.murlan.live.protocol.config.ProtocolConfig;

@Setter
@Getter
@AllArgsConstructor
public final class InformGiveCardResp implements Resp {
    private ResponseStatus responseStatus;
    private long originPlayerId;
    private long targetPlayerId;
    private Card card;
    @Accessors(fluent = true) private boolean haveBothPlayersGivenCards;

    @Override
    public String toMessage(ProtocolConfig config) {
        return String.join(
                config.getProtocol_delimiter(),
                ServerEvent.INFORM_GIVE_CARD.id(),
                responseStatus.toString(),
                String.valueOf(originPlayerId),
                String.valueOf(targetPlayerId),
                card != null ? String.valueOf(card.ordinal()) : ""
        );
    }
}
