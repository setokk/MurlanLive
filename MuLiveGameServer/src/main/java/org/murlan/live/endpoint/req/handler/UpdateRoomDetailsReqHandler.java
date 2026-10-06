package org.murlan.live.endpoint.req.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformUpdateRoomDetailsResp;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.api.UpdateRoomDetailsReq;
import org.murlan.live.protocol.api.UpdateRoomDetailsResp;
import org.murlan.live.protocol.dto.RoomDetailsDto;
import org.murlan.live.protocol.dto.UpdatedRoomDetailsDto;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public final class UpdateRoomDetailsReqHandler implements ReqHandler<UpdateRoomDetailsReq> {
    private final RoomHandler roomHandler;

    @Override
    public Class<UpdateRoomDetailsReq> requestType() {
        return UpdateRoomDetailsReq.class;
    }

    @Override
    public ReqHandlerResult handle(UpdateRoomDetailsReq req, ReqContext context) throws IOException, InterruptedException {
        Optional<UpdatedRoomDetailsDto> updatedRoomDetailsDto = Optional.empty();
        if (context.isRoomPresent()) {
            updatedRoomDetailsDto = roomHandler.updateRoom(context.room().getId(), new RoomDetailsDto(
                    req.getRoomName(),
                    req.getTotalScoreToWin(),
                    req.getTurnDurationInSeconds()
            ), context.player());
        }

        Resp informResp = null;

        boolean isSuccessful = updatedRoomDetailsDto.isPresent();
        if (isSuccessful) {
            informResp = new InformUpdateRoomDetailsResp(ResponseStatus.OK, updatedRoomDetailsDto.get());
        }

        return ReqHandlerResult.replyAndInform(new UpdateRoomDetailsResp(
                isSuccessful ? ResponseStatus.OK : ResponseStatus.ERROR,
                isSuccessful ? updatedRoomDetailsDto.get() : null
        ), informResp);
    }
}
