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
import org.murlan.live.protocol.api.InformSurrenderResp;
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

    public void closeWithErrorMessage(Session session, MuliveCloseReason closeReason) throws IOException {
        log.info("Rejecting connection with sessionId: {}, with reason: {}", session.getId(), closeReason.getMessage());
        session.close(closeReason.create());
    }

    public void informPlayers(Resp resp, PlayerSession originPlayer, List<PlayerSession> playerSessionsInRoom) throws IOException {
        String message = generator.generateMessage(resp);
        if (message.isEmpty()) {
            return;
        }

        for (PlayerSession playerSession : playerSessionsInRoom) {
            switch (resp) {
                case InformGameStartResp informGameStartResp -> {
                    CardCombination cardCombination = new CardCombination(playerSession.getPlayer().getHand());
                    informGameStartResp.getGameStateDto().setHand(cardCombination.toMessage(config.getProtocol_list_delimiter()));

                    playerSession.getSession().getBasicRemote().sendText(generator.generateMessage(informGameStartResp));
                }
                case InformGameFinishResp informGameFinishResp -> playerSession.getSession().getBasicRemote().sendText(message);
                case InformPlayHandResp informPlayHandResp -> {
                    if (!playerSession.equals(originPlayer)) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    }
                }
                case InformSurrenderResp informSurrenderResp -> {
                    if (!playerSession.equals(originPlayer)) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    }
                }
                case InformPassResp informPassResp -> {
                    if (!playerSession.equals(originPlayer)) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    }
                }
                case InformGiveCardResp informGiveCardResp -> {
                    if (informGiveCardResp.getTargetPlayerId() == playerSession.getPlayer().getId()) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    } else if (!playerSession.equals(originPlayer)) {
                        Resp hiddenInformGiveCardResp = new InformGiveCardResp(
                                ResponseStatus.OK,
                                informGiveCardResp.getOriginPlayerId(),
                                informGiveCardResp.getTargetPlayerId(),
                                null,
                                informGiveCardResp.haveBothPlayersGivenCards()
                        );
                        playerSession.getSession().getBasicRemote().sendText(generator.generateMessage(hiddenInformGiveCardResp));
                    }
                }
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
