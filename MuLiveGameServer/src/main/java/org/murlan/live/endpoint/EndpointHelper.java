package org.murlan.live.endpoint;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGameFinishResp;
import org.murlan.live.protocol.api.InformGameStartResp;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.InformPassResp;
import org.murlan.live.protocol.api.InformPlayHandResp;
import org.murlan.live.protocol.api.InformPlayerJoinRoomResp;
import org.murlan.live.protocol.api.InformPlayerLeaveRoomResp;
import org.murlan.live.protocol.api.InformPlayerLostConnectionResp;
import org.murlan.live.protocol.api.InformPlayerReadyResp;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.util.Generator;
import org.murlan.live.protocol.util.Parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class EndpointHelper {
    private static final Logger log = LogManager.getLogger(EndpointHelper.class);

    private final Parser parser;
    private final Generator generator;
    private final ProtocolConfig config;

    public void send(Resp resp, PlayerSession playerSession) throws IOException {
        String message = generator.generateMessage(resp);
        if (message.isEmpty()) {
            return;
        }

        log.info(
                "[OUT]\n-> event={}\n\t- playerId={}\n\t- payload={}\n\t- sessionId={}\n",
                resp.getClass().getSimpleName(),
                playerSession.getPlayer().getId(),
                message,
                playerSession.getSession().getId()
        );

        playerSession.getSession().getBasicRemote().sendText(message);
    }

    public void closeWithErrorMessage(Session session, MuliveCloseReason closeReason) throws IOException {
        log.info("Rejecting connection with sessionId: {}, with reason: {}", session.getId(), closeReason.getMessage());
        session.close(closeReason.create());
    }

    /**
     * Sends a server event to a list of players.
     * </br>
     * </br>
     * <b>The default behaviour of this method:</b> send the server event to everyone, <b>EXCEPT</b> the player that did this request.
     * </br>
     * <i>(ex. A player does a {@link org.murlan.live.protocol.api.PassReq} request ->
     * This has the side effect of sending {@link InformPassResp} to the 3 other players.
     * The player that did the initial request is ignored.)</i>
     * </br>
     * </br>
     * If any server event needs <b>special handling</b> and does not follow this rule
     * (see {@link InformGameStartResp}, {@link InformGameFinishResp}, {@link InformGameFinishResp}, {@link InformGiveCardResp} etc.),
     * it can be specified inside the <b>switch</b> for special handling.
     * @param resp the server event
     * @param originPlayer the origin player (the one that made the initial request)
     * @param playerSessionsInRoom the players that are in the room.
     * @throws IOException in case of any socket error
     */
    public void informPlayers(Resp resp, PlayerSession originPlayer, List<PlayerSession> playerSessionsInRoom) throws IOException {
        if (resp == null) {
            return;
        }

        for (PlayerSession playerSession : playerSessionsInRoom) {
            switch (resp) {
                case InformGameStartResp informGameStartResp -> {
                    CardCombination cardCombination = new CardCombination(playerSession.getPlayer().getHand());
                    informGameStartResp.getGameStateDto().setHand(cardCombination.toMessage(config.getProtocol_list_delimiter()));

                    send(informGameStartResp, playerSession);
                }
                case InformGameFinishResp informGameFinishResp -> send(resp, playerSession);
                case InformGiveCardResp informGiveCardResp -> {
                    if (informGiveCardResp.getTargetPlayerId() == playerSession.getPlayer().getId()) {
                        send(resp, playerSession);
                    } else if (!playerSession.equals(originPlayer)) {
                        Resp hiddenInformGiveCardResp = new InformGiveCardResp(
                                ResponseStatus.OK,
                                informGiveCardResp.getOriginPlayerId(),
                                informGiveCardResp.getTargetPlayerId(),
                                null,
                                informGiveCardResp.haveBothPlayersGivenCards()
                        );
                        send(hiddenInformGiveCardResp, playerSession);
                    }
                }
                case InformPlayerLostConnectionResp informPlayerLostConnectionResp -> send(resp, playerSession);
                default -> {
                    if (!playerSession.equals(originPlayer)) {
                        send(resp, playerSession);
                    }
                }
            }
        }
    }

    public Optional<String> getAndCheckQueryParam(String key, String queryParamString) {
        if (queryParamString == null || queryParamString.isEmpty()) {
            return Optional.empty();
        }
        Map<String, String> queryParams = parser.parseQueryParams(queryParamString);
        return Optional.ofNullable(queryParams.get(key));
    }
}
