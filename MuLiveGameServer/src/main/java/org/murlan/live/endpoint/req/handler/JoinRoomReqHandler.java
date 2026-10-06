package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPlayerJoinRoomResp;
import org.murlan.live.protocol.api.JoinRoomReq;
import org.murlan.live.protocol.api.JoinRoomResp;
import org.murlan.live.protocol.api.Resp;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public final class JoinRoomReqHandler implements ReqHandler<JoinRoomReq> {
    private final RoomHandler roomHandler;

    @Override
    public Class<JoinRoomReq> requestType() {
        return JoinRoomReq.class;
    }

    @Override
    public ReqHandlerResult handle(JoinRoomReq req, ReqContext context) throws IOException, InterruptedException {
        UUID roomId;
        try {
            roomId = UUID.fromString(req.getRoomId());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ReqHandlerResult.reply(new JoinRoomResp(ResponseStatus.ERROR));
        }

        Resp informResp = null;

        boolean isSuccessful = roomHandler.joinRoom(roomId, context.playerSession());
        if (isSuccessful) {
            informResp = new InformPlayerJoinRoomResp(ResponseStatus.OK, context.player());
        }

        return ReqHandlerResult.replyAndInform(new JoinRoomResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR
        ), informResp);
    }
}
