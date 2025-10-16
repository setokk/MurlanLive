package org.murlan.live.protocol.dto;

import java.util.Objects;

public record RoomDto(String id, String name, int numPlayers) {
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
