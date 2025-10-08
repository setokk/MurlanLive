package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class JoinRoomReq implements Req {
    private final String roomId;
    private final String passcode;

    public JoinRoomReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        if (messageParts.length != startIndex() + 2) {
            throw new InvalidDataException();
        }
        roomId = messageParts[startIndex()];
        passcode = messageParts[startIndex() + 1];
    }
}
