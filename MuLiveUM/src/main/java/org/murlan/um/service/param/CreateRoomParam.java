package org.murlan.um.service.param;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateRoomParam (
        String id,
        String name,
        boolean isPublic,
        LocalDateTime creationDate,
        short totalScoreToWin,
        short turnDurationInSeconds,
        short roundsPlayed,
        Map<Long, Short> totalScores,
        Long ownerId
) {

}
