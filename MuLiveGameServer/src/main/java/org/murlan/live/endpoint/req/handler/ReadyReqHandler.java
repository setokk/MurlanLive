package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPlayerReadyResp;
import org.murlan.live.protocol.api.ReadyReq;
import org.murlan.live.protocol.api.ReadyResp;
import org.murlan.live.protocol.api.Resp;

import java.io.IOException;

public final class ReadyReqHandler implements ReqHandler<ReadyReq> {
    @Override
    public Class<ReadyReq> requestType() {
        return ReadyReq.class;
    }

    @Override
    public ReqHandlerResult handle(ReadyReq req, ReqContext context) throws IOException, InterruptedException {
        Resp informResp = null;

        boolean isSuccessful = context.isRoomPresent() && context.room().ready(context.player());
        if (isSuccessful) {
            informResp = new InformPlayerReadyResp(ResponseStatus.OK, context.player());
        }

        return ReqHandlerResult.replyAndInform(new ReadyResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR
        ), informResp);
    }
}
