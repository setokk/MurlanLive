package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.live.config.ProtocolConfig;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.GameStateResp;
import org.murlan.live.protocol.api.PassReq;
import org.murlan.live.protocol.api.PassResp;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.PlayHandResp;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.api.SurrenderReq;
import org.murlan.live.protocol.api.SurrenderResp;

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
    GAME_STATE(GameStateReq::new, GameStateResp::new),

    /**
     * Indicates that a client wants to make a move (play their hand).
     */
    PLAY_HAND(PlayHandReq::new, PlayHandResp::new),

    /**
     * Indicates that a client doesn't want or cannot play anything at the moment.
     */
    PASS(PassReq::new, PassResp::new),

    /**
     * Indicates that a client wants to surrender.
     */
    SURRENDER(SurrenderReq::new, SurrenderResp::new);

    private final ReqFactory reqFactory;
    private final RespFactory respFactory;

    public static ClientEvent fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Invalid ordinal: " + ordinal);
        }
        return ClientEvent.values()[ordinal];
    }

    public interface ReqFactory {
        Req newReq(String[] messageParts, ProtocolConfig config);
    }

    public interface RespFactory {
        Resp newResp();
    }
}
