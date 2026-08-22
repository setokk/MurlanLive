package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public class LeaveRoomReq implements Req {
    private final String roomId;

    public LeaveRoomReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        roomId = messageParts[startIndex()];
    }
}
