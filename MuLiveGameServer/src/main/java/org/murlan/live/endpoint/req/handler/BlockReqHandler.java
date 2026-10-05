package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.BlockReq;
import org.murlan.live.protocol.api.BlockResp;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.rest.PlayerRESTClient;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public final class BlockReqHandler implements ReqHandler<BlockReq> {
    private final PlayerRESTClient playerRESTClient;

    @Override
    public Class<BlockReq> requestType() {
        return BlockReq.class;
    }

    @Override
    public ReqHandlerResult handle(BlockReq req, ReqContext context) throws IOException, InterruptedException {
        Optional<Player> blockedPlayer = playerRESTClient.blockPlayer(
                context.player().getJwt(),
                req.getPlayerToBlock()
        );

        blockedPlayer.ifPresent(p ->
                context.playerSession().getBlockedPlayers().add(p)
        );

        return ReqHandlerResult.reply(new BlockResp(
                blockedPlayer.isPresent() ? ResponseStatus.OK : ResponseStatus.ERROR,
                blockedPlayer.orElse(null)
        ));
    }
}
