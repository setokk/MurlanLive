package org.mulan.live.session;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MuLiveSession {
    private String lobbyId;
    private Session session;

    public static MuLiveSession fromSession(Session session) {
        return new MuLiveSession(null, session);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MuLiveSession that = (MuLiveSession) o;
        return session.getId().equals(that.session.getId());
    }
}
