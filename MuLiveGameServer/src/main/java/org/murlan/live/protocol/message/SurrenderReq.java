package org.murlan.live.protocol.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SurrenderReq implements Req {
    private String JWT;

    public SurrenderReq(String[] messageParts) {

    }
}
