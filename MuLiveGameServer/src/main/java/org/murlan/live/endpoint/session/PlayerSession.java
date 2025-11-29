package org.murlan.live.endpoint.session;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.dto.Player;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSession {
    private Session session;
    private Player player;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return session.getId().equals(that.session.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(session.getId());
    }
}
