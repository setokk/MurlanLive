package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.AvailableRoomsReq;
import org.murlan.live.protocol.api.AvailableRoomsResp;
import org.murlan.live.protocol.dto.RoomDto;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public final class AvailableRoomsReqHandler implements ReqHandler<AvailableRoomsReq> {
    private final RoomHandler roomHandler;

    @Override
    public Class<AvailableRoomsReq> requestType() {
        return AvailableRoomsReq.class;
    }

    @Override
    public ReqHandlerResult handle(AvailableRoomsReq req, ReqContext context) throws IOException, InterruptedException {
        List<RoomDto> availableRooms = roomHandler.getAvailableRooms();
        return ReqHandlerResult.reply(new AvailableRoomsResp(ResponseStatus.OK, availableRooms));
    }
}
