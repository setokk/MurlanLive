package org.murlan.live.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.protocol.dto.PlayerDto;

import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
public class Room {
    private final String id;
    private final String name;
    private final boolean isPublic;
    private final String passcode;
    private GameState gameState;
    private LocalDateTime creationDate;

    public boolean addPlayer(PlayerDto playerDto) {
        return gameState.addPlayer(playerDto);
    }

    public int getNumPlayers() {
        return gameState.getPlayers().size();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Room room)) return false;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
