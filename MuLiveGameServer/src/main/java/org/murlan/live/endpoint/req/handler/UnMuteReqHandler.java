package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.UnMuteReq;
import org.murlan.live.protocol.api.UnMuteResp;
import org.murlan.live.protocol.dto.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

public final class UnMuteReqHandler implements ReqHandler<UnMuteReq> {
    @Override
    public Class<UnMuteReq> requestType() {
        return UnMuteReq.class;
    }

    @Override
    public ReqHandlerResult handle(UnMuteReq req, ReqContext context) throws IOException, InterruptedException {
        boolean isSuccessful = false;
        if (context.isRoomPresent()) {
            context.playerSession().getMutedPlayers().removeAll(req.getPlayersToUnMute());
            isSuccessful = true;
        }

        return ReqHandlerResult.reply(new UnMuteResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                isSuccessful ? req.getPlayersToUnMute().stream().map(Player::getId).collect(Collectors.toSet()) : Collections.emptySet()
        ));
    }
}
