package org.murlan.live.protocol.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.config.ProtocolConfig;

@Setter
@Getter
@NoArgsConstructor
public class GameStateReq implements Req {
    private String JWT;

    public GameStateReq(String[] messageParts, ProtocolConfig config) {

    }
}
