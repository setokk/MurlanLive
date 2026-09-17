package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.game.GameConstants;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

@Getter
public final class ChatReq implements Req {
    private final String message;

    public ChatReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        message = messageParts[startIndex()];
    }

    @Override
    public void postValidate() throws InvalidDataException {
        if (message == null || message.isBlank()) {
            throw new InvalidDataException();
        }

        if (message.length() > GameConstants.CHAT_CHARACTER_LIMIT) {
            throw new InvalidDataException();
        }
    }
}
