package org.murlan.live.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.session.player.PlayerDto;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Room {
    private final String id;
    private final String name;
    private final boolean isPublic;
    private final String passcode;
    private GameState gameState;

    public boolean addPlayer(PlayerDto playerDto) {
        return this.gameState.addPlayer(playerDto);
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
