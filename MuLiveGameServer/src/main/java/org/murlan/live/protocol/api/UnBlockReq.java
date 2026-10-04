package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;

@Getter
public final class UnBlockReq implements Req {
    private final Player playerToUnblock;

    public UnBlockReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        playerToUnblock = new Player(parseNumber(messageParts[startIndex()], -1));
    }
}
