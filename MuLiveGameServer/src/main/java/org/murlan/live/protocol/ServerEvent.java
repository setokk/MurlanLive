package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum defining <b>all</b> possible events that the server can send during a game lobby.
 */
@Getter
@AllArgsConstructor
public enum ServerEvent {
    INFORM_PLAY_HAND(),
    INFORM_PASS(),
    INFORM_GIVE_CARD(),
    INFORM_GAME_START(),
    INFORM_GAME_FINISH(),
    INFORM_PLAYER_JOIN_ROOM(),
    INFORM_PLAYER_LEAVE_ROOM(),
    INFORM_PLAYER_LOST_CONNECTION();

    public String id() {
        return "S" + ordinal();
    }
}
