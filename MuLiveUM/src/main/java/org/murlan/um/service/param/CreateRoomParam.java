package org.murlan.um.service.param;

import org.murlan.um.model.dto.PlayerDto;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateRoomParam (
        String id,
        String name,
        boolean isPublic,
        LocalDateTime creationDate,
        short totalScoreToWin,
        short roundsPlayed,
        Map<Long, Short> totalScores,
        Long ownerId
) {

}
