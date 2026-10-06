package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.UnBlockReq;
import org.murlan.live.protocol.api.UnBlockResp;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.rest.PlayerRESTClient;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public final class UnBlockReqHandler implements ReqHandler<UnBlockReq> {
    private final PlayerRESTClient playerRESTClient;

    @Override
    public Class<UnBlockReq> requestType() {
        return UnBlockReq.class;
    }

    @Override
    public ReqHandlerResult handle(UnBlockReq req, ReqContext context) throws IOException, InterruptedException {
        Optional<Player> unblockedPlayer = playerRESTClient.unblockPlayer(
                context.player().getJwt(),
                req.getPlayerToUnblock()
        );

        unblockedPlayer.ifPresent(p ->
                context.playerSession().getBlockedPlayers().remove(p)
        );

        return ReqHandlerResult.reply(new UnBlockResp(
                unblockedPlayer.isPresent() ? ResponseStatus.OK : ResponseStatus.ERROR,
                unblockedPlayer.orElse(null)
        ));
    }
}
