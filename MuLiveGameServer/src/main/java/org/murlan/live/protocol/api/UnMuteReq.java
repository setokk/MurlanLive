package org.murlan.live.protocol.api;

import lombok.Getter;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.Player;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public final class UnMuteReq implements Req {
    private final Set<Player> playersToUnMute;

    public UnMuteReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException {
        validate(messageParts);
        playersToUnMute = Arrays.stream(messageParts[startIndex()].split(Pattern.quote(config.getProtocol_list_delimiter())))
                .map(Long::valueOf)
                .map(Player::new)
                .collect(Collectors.toSet());
    }
}
