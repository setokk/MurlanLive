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
                case InformPlayHandResp informPlayHandResp -> {
                    if (!playerSession.equals(originPlayer)) {
                        send(resp, playerSession);
                    }
                }
                case InformPassResp informPassResp -> {
                    if (!playerSession.equals(originPlayer)) {
                        send(resp, playerSession);
                    }
                }
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
                case InformPlayerJoinRoomResp informPlayerJoinRoomResp -> send(resp, playerSession);
                case InformPlayerLeaveRoomResp informPlayerLeaveRoomResp -> send(resp, playerSession);
                case InformPlayerLostConnectionResp informPlayerLostConnectionResp -> send(resp, playerSession);
                default -> throw new IllegalStateException("Unexpected value: " + resp);
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
