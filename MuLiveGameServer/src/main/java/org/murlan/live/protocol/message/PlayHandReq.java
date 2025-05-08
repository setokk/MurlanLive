package org.murlan.live.protocol.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PlayHandReq implements Req {
    private String JWT;

    public PlayHandReq(String[] messageParts) {

    }
}
