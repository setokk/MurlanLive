package org.murlan.live.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.live.protocol.api.AvailableRoomsReq;
import org.murlan.live.protocol.api.CreateRoomReq;
import org.murlan.live.protocol.api.GameStateReq;
import org.murlan.live.protocol.api.GiveCardReq;
import org.murlan.live.protocol.api.JoinRoomReq;
import org.murlan.live.protocol.api.LeaveRoomReq;
import org.murlan.live.protocol.api.PassReq;
import org.murlan.live.protocol.api.PlayHandReq;
import org.murlan.live.protocol.api.Req;
import org.murlan.live.protocol.api.error.InvalidDataException;
import org.murlan.live.protocol.config.ProtocolConfig;

import java.util.Arrays;

/**
 * Enum defining <b>all</b> possible events that a player can take during a game lobby.
 * </br>
 * List of possible requested actions:
 * <ul>
 *     <li>{@link #GAME_STATE}</li>
 *     <li>{@link #PLAY_HAND}</li>
 *     <li>{@link #PASS}</li>
 *     <li>${@link #AVAILABLE_ROOMS}</li>
 *     <li>${@link #JOIN_ROOM}</li>
 *     <li>${@link #CREATE_ROOM}</li>
 *     <li>${@link #GIVE_CARD}</li>
 *     <li>${@link #LEAVE_ROOM}</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum ClientEvent {
    /**
     * Indicates that a client wants to be informed about the game state (who's turn it is to play etc.).
     */
    GAME_STATE(GameStateReq::new),

    /**
     * Indicates that a client wants to make a move (play their hand).
     */
    PLAY_HAND(PlayHandReq::new),

    /**
     * Indicates that a client doesn't want or cannot play anything at the moment.
     */
    PASS(PassReq::new),

    /**
     * Indicates that a client wants to know about the available public rooms.
     */
    AVAILABLE_ROOMS(AvailableRoomsReq::new),

    /**
     * Indicates that a client wants to join a specific room.
     */
    JOIN_ROOM(JoinRoomReq::new),

    /**
     * Indicates that a client wants to create a new room.
     */
    CREATE_ROOM(CreateRoomReq::new),

    /**
     * Indicates that a player (winner or loser from previous game) wants to give a card to the other.
     */
    GIVE_CARD(GiveCardReq::new),

    /**
     * Indicates that a client wants to leave a room.
     */
    LEAVE_ROOM(LeaveRoomReq::new);

    private final ReqFactory reqFactory;

    public String id() {
        return "C" + ordinal();
    }

    public static ClientEvent fromId(String id) {
        return Arrays.stream(ClientEvent.values())
                .filter(c -> c.id().equals(id))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown event " + id));
    }

    public interface ReqFactory {
        Req newReq(String[] messageParts, ProtocolConfig config) throws InvalidDataException;
    }
}
