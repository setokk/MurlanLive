package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.ChatReq;
import org.murlan.live.protocol.api.ChatResp;
import org.murlan.live.protocol.api.InformPlayerChatResp;
import org.murlan.live.protocol.api.Resp;

import java.io.IOException;

public final class ChatReqHandler implements ReqHandler<ChatReq> {
    @Override
    public Class<ChatReq> requestType() {
        return ChatReq.class;
    }

    @Override
    public ReqHandlerResult handle(ChatReq req, ReqContext context) throws IOException, InterruptedException {
        Resp informResp = null;

        if (context.isRoomPresent()) {
            informResp = new InformPlayerChatResp(ResponseStatus.OK, req.getMessage(), context.player());
        }

        return ReqHandlerResult.replyAndInform(new ChatResp(
                context.isRoomPresent() ? ResponseStatus.OK : ResponseStatus.ERROR
        ), informResp);
    }
}
