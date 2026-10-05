package org.murlan.live.protocol.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.Player;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public final class UMCreateRoomRequest {
    private final String id;
    private final String name;
    @JsonProperty("isPublic")
    private final boolean isPublic;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private final LocalDateTime creationDate;
    private final short totalScoreToWin;
    private final short roundsPlayed;
    private final Map<Long, Short> totalScores;
    private final Long ownerId;

    public static UMCreateRoomRequest createFrom(Room room) {
        return new UMCreateRoomRequest(
                room.getId().toString(),
                room.getName(),
                room.isPublic(),
                room.getCreationDate(),
                room.getTotalScoreToWin(),
                (short) room.getGameStates().size(),
                room.getTotalScores().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().getId(),
                                Map.Entry::getValue,
                                (existing, replacement) -> existing,
                                LinkedHashMap::new
                        )),
                room.getOwner().getId()
        );
    }
}
