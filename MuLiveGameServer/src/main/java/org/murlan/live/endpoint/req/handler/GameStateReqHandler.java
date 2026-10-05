package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.GameStateResp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.GameStateDto;

import java.io.IOException;

@RequiredArgsConstructor
public final class GameStateReqHandler implements ReqHandler<GameStateReq> {
    private final ProtocolConfig config;

    @Override
    public Class<GameStateReq> requestType() {
        return GameStateReq.class;
    }

    @Override
    public ReqHandlerResult handle(GameStateReq req, ReqContext context) throws IOException, InterruptedException {
        GameStateDto gameStateDto = null;
        if (context.isRoomPresent()) {
            gameStateDto = GameStateDto.from(context.room(), context.player(), config);
        }

        return ReqHandlerResult.reply(new GameStateResp(
                context.isRoomPresent() ? ResponseStatus.OK : ResponseStatus.ERROR,
                gameStateDto
        ));
    }
}
