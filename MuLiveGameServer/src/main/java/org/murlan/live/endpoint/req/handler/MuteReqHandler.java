package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.MuteReq;
import org.murlan.live.protocol.api.MuteResp;
import org.murlan.live.protocol.dto.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

public final class MuteReqHandler implements ReqHandler<MuteReq> {
    @Override
    public Class<MuteReq> requestType() {
        return MuteReq.class;
    }

    @Override
    public ReqHandlerResult handle(MuteReq req, ReqContext context) throws IOException, InterruptedException {
        boolean isSuccessful = false;
        if (context.isRoomPresent()) {
            context.playerSession().getMutedPlayers().addAll(req.getPlayersToMute());
            isSuccessful = true;
        }

        return ReqHandlerResult.reply(new MuteResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                isSuccessful ? req.getPlayersToMute().stream().map(Player::getId).collect(Collectors.toSet()) : Collections.emptySet()
        ));
    }
}
