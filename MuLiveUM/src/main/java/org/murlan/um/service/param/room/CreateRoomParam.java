package org.murlan.um.service.param.room;

import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.api.request.gameserver.GameState;

import java.util.List;

public record CreateRoomParam (
        String id,
        String name,
        boolean isPublic,
        short totalScoreToWin,
        List<GameState> gameStates,
        PlayerDto owner
) {

}
