package org.murlan.live.session;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSession {
    private Session session;
    private PlayerInfo playerInfo;

    public static PlayerSession fromSession(Session session) {
        return new PlayerSession(session, null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return session.getId().equals(that.session.getId());
    }
}
