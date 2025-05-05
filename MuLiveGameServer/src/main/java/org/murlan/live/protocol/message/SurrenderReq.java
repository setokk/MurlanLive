package org.murlan.live.protocol.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurrenderReq implements Req {
    private String JWT;
}
