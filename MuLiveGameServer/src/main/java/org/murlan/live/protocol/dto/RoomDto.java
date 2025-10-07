package org.murlan.live.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class RoomDto {
    private final String id;
    private final String name;
    private final int numPlayers;

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
