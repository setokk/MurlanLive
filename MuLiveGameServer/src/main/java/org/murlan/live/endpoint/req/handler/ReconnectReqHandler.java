package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPlayerReconnectedResp;
import org.murlan.live.protocol.api.ReconnectReq;
import org.murlan.live.protocol.api.ReconnectResp;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.GameStateDto;
import org.murlan.live.protocol.dto.RoomDto;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public final class ReconnectReqHandler implements ReqHandler<ReconnectReq> {
    private final RoomHandler roomHandler;

    @Override
    public Class<ReconnectReq> requestType() {
        return ReconnectReq.class;
    }

    @Override
    public ReqHandlerResult handle(ReconnectReq req, ReqContext context) throws IOException, InterruptedException {
        Optional<Room> optionalRoom = roomHandler.reconnectPlayer(context.playerSession());
        if (optionalRoom.isEmpty()) {
            return ReqHandlerResult.reply(new ReconnectResp(ResponseStatus.ERROR, RoomDto.invalid()));
        }

        Room room = optionalRoom.get();

        Resp informResp = new InformPlayerReconnectedResp(ResponseStatus.OK, context.player());

        return ReqHandlerResult.replyAndInform(
                new ReconnectResp(ResponseStatus.OK, RoomDto.fromRoom(room)),
                informResp
        );
    }
}
