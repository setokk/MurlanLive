package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPlayHandResp;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.PlayHandResp;
import org.murlan.live.protocol.api.Resp;

import java.io.IOException;

public final class PlayHandReqHandler implements ReqHandler<PlayHandReq> {
    @Override
    public Class<PlayHandReq> requestType() {
        return PlayHandReq.class;
    }

    @Override
    public ReqHandlerResult handle(PlayHandReq req, ReqContext context) throws IOException, InterruptedException {
        Resp informResp = null;

        boolean isSuccessful = context.isRoomPresent() && context.room().playHand(context.player(), req.getCardCombination());
        if (isSuccessful) {
            informResp = new InformPlayHandResp(ResponseStatus.OK, context.player().getId(), req.getCardCombination());
        }

        return ReqHandlerResult.replyAndInform(new PlayHandResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                req.getCardCombination()
        ), informResp);
    }
}
