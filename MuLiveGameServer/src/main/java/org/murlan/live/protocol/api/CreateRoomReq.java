package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class CreateRoomReq implements Req {
    private final String roomName;
    private final boolean isPublic;
    private final String passcode;

    public CreateRoomReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        if (messageParts.length != startIndex() + 3) {
            throw new InvalidDataException();
        }
        roomName = messageParts[startIndex()];
        isPublic = Boolean.parseBoolean(messageParts[startIndex() + 1]);
        passcode = messageParts[startIndex() + 2];
    }
}
