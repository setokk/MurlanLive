package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public final class MuteReq implements Req {
    private final List<Player> players;

    public MuteReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        players = Arrays.stream(messageParts[startIndex()].split(Pattern.quote(config.getProtocol_list_delimiter())))
                .map(Long::valueOf)
                .map(Player::new)
                .toList();
    }
}
