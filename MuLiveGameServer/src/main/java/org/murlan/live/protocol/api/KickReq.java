package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;

@Getter
public final class KickReq implements Req {
    private final long playerToKickId;

    public KickReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        playerToKickId = parseNumber(messageParts[startIndex()], Long.MIN_VALUE);
    }
}
