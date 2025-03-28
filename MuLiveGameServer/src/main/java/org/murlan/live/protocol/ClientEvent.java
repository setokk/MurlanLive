package org.murlan.live.protocol;

/**
 * Enum defining <b>all</b> possible events that a player can take during a game lobby.
 * </br>
 * List of possible requested actions:
 * <ul>
 *     <li>{@link #GAME_STATE}</li>
 *     <li>{@link #PLAY_HAND}</li>
 *     <li>{@link #PASS}</li>
 *     <li>{@link #SURRENDER}</li>
 * </ul>
 */
public enum ClientEvent {
    /**
     * Indicates that a client wants to be informed about the game state (who's turn it is to play etc.).
     */
    GAME_STATE(),

    /**
     * Indicates that a client wants to make a move (play their hand).
     */
    PLAY_HAND(),

    /**
     * Indicates that a client wants or cannot play anything at the moment.
     */
    PASS(),

    /**
     * Indicates that a client wants to surrender.
     */
    SURRENDER()
}
