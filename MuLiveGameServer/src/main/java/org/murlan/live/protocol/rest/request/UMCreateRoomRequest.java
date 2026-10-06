package org.murlan.live.protocol.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private final List<GameState> gameStates;
    private final Player owner;
    private final Map<Player, Short> totalScores;

    public static UMCreateRoomRequest createFrom(Room room) {
        return new UMCreateRoomRequest(
                room.getId().toString(),
                room.getName(),
                room.isPublic(),
                room.getCreationDate(),
                room.getTotalScoreToWin(),
                room.getGameStates(),
                room.getOwner(),
                room.getTotalScores()
        );
    }
}
