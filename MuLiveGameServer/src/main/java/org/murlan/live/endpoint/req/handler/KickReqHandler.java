package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPlayerKickedResp;
import org.murlan.live.protocol.api.KickReq;
import org.murlan.live.protocol.api.KickResp;
import org.murlan.live.protocol.api.Resp;

import java.io.IOException;

@RequiredArgsConstructor
public final class KickReqHandler implements ReqHandler<KickReq> {
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;

    @Override
    public Class<KickReq> requestType() {
        return KickReq.class;
    }

    @Override
    public ReqHandlerResult handle(KickReq req, ReqContext context) throws IOException, InterruptedException {
        PlayerSession kickedPlayerSession = null;
        if (context.isRoomPresent()) {
            kickedPlayerSession = roomHandler.kickPlayer(context.room().getId(), req.getPlayerToKickId(), context.player());
        }

        Resp informResp = null;

        boolean isSuccessful = kickedPlayerSession != null;
        if (isSuccessful) {
            informResp = new InformPlayerKickedResp(ResponseStatus.OK, kickedPlayerSession.getPlayer());
            endpointHelper.send(informResp, kickedPlayerSession); // send here because they are removed and unreachable from roomHandler.getPlayersInRoom
        }

        return ReqHandlerResult.replyAndInform(new KickResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                kickedPlayerSession != null ? kickedPlayerSession.getPlayer() : null
        ), informResp);
    }
}
