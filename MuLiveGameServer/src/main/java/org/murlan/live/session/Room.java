package org.murlan.live.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.GameState;

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

    public static Room fromId(String id) {
        return new Room(id, null, false, null, null);
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
