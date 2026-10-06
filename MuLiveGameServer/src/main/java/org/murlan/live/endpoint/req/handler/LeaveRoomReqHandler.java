package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformPlayerLeaveRoomResp;
import org.murlan.live.protocol.api.LeaveRoomReq;
import org.murlan.live.protocol.api.LeaveRoomResp;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public final class LeaveRoomReqHandler implements ReqHandler<LeaveRoomReq> {
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;

    @Override
    public Class<LeaveRoomReq> requestType() {
        return LeaveRoomReq.class;
    }

    @Override
    public ReqHandlerResult handle(LeaveRoomReq req, ReqContext context) throws IOException, InterruptedException {
        if (!context.isRoomPresent()) {
            return ReqHandlerResult.reply(new LeaveRoomResp(ResponseStatus.ERROR));
        }

        Optional<List<PlayerSession>> playersInRoom = roomHandler.removeSession(context.playerSession(), false, (r) -> {});

        boolean isSuccessful = playersInRoom.isPresent();
        if (isSuccessful) {
            InformPlayerLeaveRoomResp informPlayerLeaveRoomResp = new InformPlayerLeaveRoomResp(ResponseStatus.OK, context.player().getId());
            endpointHelper.informPlayers(informPlayerLeaveRoomResp, null, playersInRoom.get());
        }

        return ReqHandlerResult.reply(new LeaveRoomResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR
        ));
    }
}
