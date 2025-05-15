package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.message.GameStateReq;
import org.murlan.live.protocol.message.GameStateResp;
import org.murlan.live.protocol.message.PassReq;
import org.murlan.live.protocol.message.PassResp;
import org.murlan.live.protocol.message.PlayHandReq;
import org.murlan.live.protocol.message.PlayHandResp;
import org.murlan.live.protocol.message.Req;
import org.murlan.live.protocol.message.Resp;
import org.murlan.live.protocol.message.SurrenderReq;
import org.murlan.live.protocol.message.SurrenderResp;

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
@Getter
@AllArgsConstructor
public enum ClientEvent {
    /**
     * Indicates that a client wants to be informed about the game state (who's turn it is to play etc.).
     */
    GAME_STATE(GameStateReq::new, new GameStateResp()),

    /**
     * Indicates that a client wants to make a move (play their hand).
     */
    PLAY_HAND(PlayHandReq::new, new PlayHandResp()),

    /**
     * Indicates that a client wants or cannot play anything at the moment.
     */
    PASS(PassReq::new, new PassResp()),

    /**
     * Indicates that a client wants to surrender.
     */
    SURRENDER(SurrenderReq::new, new SurrenderResp());

    private final ReqFactory requestFactory;
    private final Resp response;

    public static ClientEvent fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
        return ClientEvent.values()[ordinal];
    }

    interface ReqFactory {
        Req newReq(String[] messageParts, ProtocolConfig config);
    }
}
