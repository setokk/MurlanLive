package org.murlan.um.service.param.room;

import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.api.dto.GameStateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record CreateRoomParam (
        String id,
        String name,
        boolean isPublic,
        LocalDateTime creationDate,
        short totalScoreToWin,
        List<GameStateDto> gameStates,
        Map<PlayerDto, Short> totalScores,
        Short numPlayers,
        PlayerDto owner
) {

}
