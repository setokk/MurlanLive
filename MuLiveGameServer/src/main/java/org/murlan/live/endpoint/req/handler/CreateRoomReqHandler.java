package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.logic.GameStateFactory;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.CreateRoomReq;
import org.murlan.live.protocol.api.CreateRoomResp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.RoomDto;
import org.murlan.live.protocol.rest.RoomRESTClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
public final class CreateRoomReqHandler implements ReqHandler<CreateRoomReq> {
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;
    private final RoomRESTClient roomRESTClient;
    private final ProtocolConfig config;
    private final ScheduledExecutorService scheduler;

    @Override
    public Class<CreateRoomReq> requestType() {
        return CreateRoomReq.class;
    }

    @Override
    public ReqHandlerResult handle(CreateRoomReq req, ReqContext context) throws IOException, InterruptedException {
        Room newRoom = new Room(
                req.getRoomName(),
                req.isPublic(),
                LocalDateTime.now(),
                req.getTotalScoreToWin(),
                context.player(),
                req.getTurnDurationInSeconds(),
                new GameStateFactory(roomHandler, endpointHelper, roomRESTClient, config, scheduler)
        );

        RoomDto roomDto = roomHandler.createRoom(newRoom, context.playerSession());
        return ReqHandlerResult.reply(new CreateRoomResp(
                roomDto.isValid() ? ResponseStatus.OK : ResponseStatus.ERROR,
                roomDto
        ));
    }
}
