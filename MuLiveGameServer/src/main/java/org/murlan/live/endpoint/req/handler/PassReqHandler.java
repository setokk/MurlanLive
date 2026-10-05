package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPassResp;
import org.murlan.live.protocol.api.PassReq;
import org.murlan.live.protocol.api.PassResp;
import org.murlan.live.protocol.api.Resp;

import java.io.IOException;

public final class PassReqHandler implements ReqHandler<PassReq> {
    @Override
    public Class<PassReq> requestType() {
        return PassReq.class;
    }

    @Override
    public ReqHandlerResult handle(PassReq req, ReqContext context) throws IOException, InterruptedException {
        Resp informResp = null;

        boolean isSuccessful = context.isRoomPresent() && context.room().pass(context.player());
        if (isSuccessful) {
            boolean canCurrPlayerPlayAnyHand = context.room().getActiveGameState().getPassCounter().getCounter() == 0;
            informResp = new InformPassResp(ResponseStatus.OK, context.player().getId(), canCurrPlayerPlayAnyHand);
        }

        return ReqHandlerResult.replyAndInform(new PassResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR
        ), informResp);
    }
}
