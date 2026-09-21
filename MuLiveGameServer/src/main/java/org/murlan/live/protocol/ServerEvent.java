package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum defining <b>all</b> possible events that the server can send during a game lobby.
 * </br>
 * List of possible events a player can receive:
 * <ul>
 *     <li>{@link #INFORM_PLAY_HAND}</li>
 *     <li>{@link #INFORM_PASS}</li>
 *     <li>{@link #INFORM_GIVE_CARD}</li>
 *     <li>{@link #INFORM_GAME_START}</li>
 *     <li>{@link #INFORM_GAME_FINISH}</li>
 *     <li>{@link #INFORM_PLAYER_JOIN_ROOM}</li>
 *     <li>{@link #INFORM_PLAYER_LEAVE_ROOM}</li>
 *     <li>{@link #INFORM_PLAYER_LOST_CONNECTION}</li>
 *     <li>{@link #INFORM_PLAYER_READY}</li>
 *     <li>{@link #INFORM_PLAYER_CHAT}</li>
 *     <li>{@link #INFORM_UPDATE_ROOM_DETAILS}</li>
 *     <li>{@link #INFORM_PLAYER_KICKED}</li>
 *  * </ul>
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
    INFORM_PLAYER_LOST_CONNECTION(),
    INFORM_PLAYER_READY(),
    INFORM_PLAYER_CHAT(),
    INFORM_UPDATE_ROOM_DETAILS(),
    INFORM_PLAYER_KICKED();

    public String id() {
        return "S" + ordinal();
    }
}
