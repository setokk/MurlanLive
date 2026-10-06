package org.murlan.live.endpoint.req;

import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.dto.Player;

public record ReqContext(PlayerSession playerSession, Room room) {
    public Player player() {
        return playerSession.getPlayer();
    }

    public boolean isRoomPresent() {
        return room != null;
    }
}
