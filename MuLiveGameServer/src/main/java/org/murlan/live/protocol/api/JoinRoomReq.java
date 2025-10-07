package org.murlan.live.protocol.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Setter
@Getter
@AllArgsConstructor
public class JoinRoomReq implements Req {
    private String JWT;
    private String roomId;
    private String passcode;

    public JoinRoomReq(String[] messageParts, ProtocolConfig config) {
        if (messageParts.length != startIndex() + 2) {
            throw new InvalidDataException();
        }
        roomId = messageParts[startIndex()];
        passcode = messageParts[startIndex() + 1];
    }
}
