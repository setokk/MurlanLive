package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.game.GameConstants;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class UpdateRoomDetailsReq implements Req {
    private final String roomName;
    private final short totalScoreToWin;
    private final long turnDurationInSeconds;

    public UpdateRoomDetailsReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        roomName = messageParts[startIndex()];
        totalScoreToWin = parseNumber(messageParts[startIndex() + 1], Short.MIN_VALUE);
        turnDurationInSeconds = parseNumber(messageParts[startIndex() + 2], Long.MIN_VALUE);
    }

    @Override
    public void postValidate() throws InvalidDataException {
        if (totalScoreToWin < 3 || totalScoreToWin > GameConstants.MAX_TOTAL_SCORE_TO_WIN) {
            throw new InvalidDataException();
        }

        if (!GameConstants.TURN_DURATION_SECONDS_VALUES.contains(turnDurationInSeconds)) {
            throw new InvalidDataException();
        }
    }
}
