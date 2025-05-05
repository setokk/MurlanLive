package org.murlan.live.protocol.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameStateReq implements Req {
    private String JWT;
}
