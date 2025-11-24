package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.game.GameConstants;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class CreateRoomReq implements Req {
    private final String roomName;
    private final boolean isPublic;
    private final String passcode;
    private final short totalScoreToWin;

    public CreateRoomReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        roomName = messageParts[startIndex()];
        isPublic = Boolean.parseBoolean(messageParts[startIndex() + 1]);
        passcode = messageParts[startIndex() + 2];
        totalScoreToWin = Short.parseShort(messageParts[startIndex() + 3]);
    }

    @Override
    public void validate(String[] messageParts) throws InvalidDataException {
        Req.super.validate(messageParts);
        if (totalScoreToWin > GameConstants.MAX_TOTAL_SCORE_TO_WIN) {
            throw new InvalidDataException();
        }
    }
}
