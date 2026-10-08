package org.murlan.live.protocol.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.murlan.live.game.logic.Room;

import java.util.List;
import java.util.Objects;

public record RoomDto(String id, String name, List<PlayerMinimizedDto> players, short totalScoreToWin, long turnDurationInSeconds) {
    public static RoomDto invalid() {
        return new RoomDto(null, null, null, (short) -1, -1L);
    }

    @JsonIgnore
    public boolean isValid() {
        return id != null;
    }

    @JsonIgnore
    public static RoomDto fromRoom(Room room) {
        return new RoomDto(
                room.getId().toString(),
                room.getName(),
                room.getPlayers().stream().map(PlayerMinimizedDto::fromPlayer).toList(),
                room.getTotalScoreToWin(),
                room.getTurnDurationInSeconds()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoomDto roomDto)) return false;
        return Objects.equals(id, roomDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
