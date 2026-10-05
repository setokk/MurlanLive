package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.GiveCardReq;
import org.murlan.live.protocol.api.GiveCardResp;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.dto.Player;

import java.io.IOException;

public final class GiveCardReqHandler implements ReqHandler<GiveCardReq> {
    @Override
    public Class<GiveCardReq> requestType() {
        return GiveCardReq.class;
    }

    @Override
    public ReqHandlerResult handle(GiveCardReq req, ReqContext context) throws IOException, InterruptedException {
        Player receivingPlayer = new Player(req.getReceivingPlayerId());

        boolean haveBothPlayerGivenCards = false;

        Resp informResp = null;

        boolean isSuccessful = context.isRoomPresent() && context.room().giveCard(req.getCard(), context.player(), receivingPlayer);
        if (isSuccessful) {
            haveBothPlayerGivenCards = context.room().getActiveGameState().haveBothPlayersGivenCards();
            informResp = new InformGiveCardResp(ResponseStatus.OK,
                    context.player().getId(),
                    receivingPlayer.getId(),
                    req.getCard(),
                    haveBothPlayerGivenCards
            );
        }

        return ReqHandlerResult.replyAndInform(new GiveCardResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                haveBothPlayerGivenCards,
                req.getCard()
        ), informResp);
    }
}
