package org.murlan.live.protocol.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Setter
@Getter
@NoArgsConstructor
public class CreateRoomReq implements Req {
    private String JWT;
    private String roomName;
    private boolean isPublic;
    private String passcode;

    public CreateRoomReq(String[] messageParts, ProtocolConfig config) {
        if (messageParts.length != startIndex() + 3) {
            throw new InvalidDataException();
        }
        roomName = messageParts[startIndex()];
        isPublic = Boolean.parseBoolean(messageParts[startIndex() + 1]);
        passcode = messageParts[startIndex() + 2];
    }
}
