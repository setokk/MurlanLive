package org.murlan.live.endpoint.req;

import org.murlan.live.protocol.api.Resp;

public record ReqHandlerResult(Resp resp, Resp informResp) {
    public static ReqHandlerResult reply(Resp resp) {
        return new ReqHandlerResult(resp, null);
    }

    public static ReqHandlerResult replyAndInform(Resp r, Resp i) {
        return new ReqHandlerResult(r, i);
    }
}
