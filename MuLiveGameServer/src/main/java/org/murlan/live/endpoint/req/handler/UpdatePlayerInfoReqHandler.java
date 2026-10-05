package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformUpdatePlayerInfoResp;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.api.UpdatePlayerInfoReq;
import org.murlan.live.protocol.api.UpdatePlayerInfoResp;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.rest.PlayerRESTClient;
import org.murlan.live.protocol.rest.request.UMUpdatePlayerInfoRequest;

import java.io.IOException;

@RequiredArgsConstructor
public final class UpdatePlayerInfoReqHandler implements ReqHandler<UpdatePlayerInfoReq> {
    private final PlayerRESTClient playerRESTClient;

    @Override
    public Class<UpdatePlayerInfoReq> requestType() {
        return UpdatePlayerInfoReq.class;
    }

    @Override
    public ReqHandlerResult handle(UpdatePlayerInfoReq req, ReqContext context) throws IOException, InterruptedException {
        boolean isSuccessful = playerRESTClient.updatePlayerInfo(
                context.player().getJwt(),
                new UMUpdatePlayerInfoRequest(req.getProfileIconId())
        );

        Resp informResp = null;

        Player playerToUpdate = null;
        if (isSuccessful) {
            playerToUpdate = context.player();
            playerToUpdate.setProfileIconId(req.getProfileIconId());
            informResp = new InformUpdatePlayerInfoResp(ResponseStatus.OK, playerToUpdate);
        }

        return ReqHandlerResult.replyAndInform(new UpdatePlayerInfoResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                playerToUpdate
        ), informResp);
    }
}
