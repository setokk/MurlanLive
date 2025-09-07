package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum defining <b>all</b> possible events that the server can send during a game lobby.
 * </br>
 * List of possible requested actions:
 * <ul>
 *     <li>{@link #GAME_STATE}</li>
 *     <li>{@link #PLAY_HAND}</li>
 *     <li>{@link #PASS}</li>
 *     <li>{@link #SURRENDER}</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum ServerEvent {

}
