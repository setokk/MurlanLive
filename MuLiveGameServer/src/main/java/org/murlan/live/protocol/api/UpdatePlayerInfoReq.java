package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class UpdatePlayerInfoReq implements Req {
    private final long profileIconId;

    public UpdatePlayerInfoReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        profileIconId = parseNumber(messageParts[startIndex()], -1);
    }

    @Override
    public void postValidate() throws InvalidDataException {
        if (profileIconId < 0) {
            throw new InvalidDataException();
        }
    }
}
