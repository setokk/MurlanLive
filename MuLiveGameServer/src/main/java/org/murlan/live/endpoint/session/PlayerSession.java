package org.murlan.live.endpoint.session;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.dto.Player;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSession {
    private Session session;
    private Player player;
    private Set<Player> mutedPlayers;
    private Set<Player> blockedPlayers;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return this.player.equals(that.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.player.hashCode());
    }
}
