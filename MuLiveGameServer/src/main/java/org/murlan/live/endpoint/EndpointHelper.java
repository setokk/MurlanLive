package org.murlan.live.endpoint;

import jakarta.websocket.Session;
import lombok.AllArgsConstructor;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGameFinishResp;
import org.murlan.live.protocol.api.InformGameStartResp;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.InformPassResp;
import org.murlan.live.protocol.api.InformPlayHandResp;
import org.murlan.live.protocol.api.InformSurrenderResp;
import org.murlan.live.protocol.api.Resp;
import org.murlan.live.protocol.api.error.GenericErrorResp;
import org.murlan.live.protocol.util.Generator;
import org.murlan.live.protocol.util.Parser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class EndpointHelper {
    private final Parser parser;
    private final Generator generator;

    public void sendErrorMessage(Session session, GenericErrorResp resp) throws IOException {
        String errorMessage = generator.generateMessage(resp);
        session.getBasicRemote().sendText(errorMessage);
    }

    public Optional<String> getAndCheckQueryParam(String key, String queryParamString) {
        if (queryParamString == null || queryParamString.isEmpty()) {
            return Optional.empty();
        }
        Map<String, String> queryParams = parser.parseQueryParams(queryParamString);
        return Optional.ofNullable(queryParams.get(key));
    }

    public void informPlayers(Resp resp, PlayerSession originPlayer, List<PlayerSession> playerSessionsInRoom) throws IOException {
        String message = generator.generateMessage(resp);
        if (message.isEmpty()) {
            return;
        }

        for (PlayerSession playerSession : playerSessionsInRoom) {
            switch (resp) {
                case InformGameStartResp informGameStartResp -> playerSession.getSession().getBasicRemote().sendText(message);
                case InformGameFinishResp informGameFinishResp -> playerSession.getSession().getBasicRemote().sendText(message);
                case InformPlayHandResp informPlayHandResp -> {
                    if (!originPlayer.equals(playerSession)) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    }
                }
                case InformSurrenderResp informSurrenderResp -> {
                    if (!originPlayer.equals(playerSession)) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    }
                }
                case InformPassResp informPassResp -> {
                    if (!originPlayer.equals(playerSession)) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    }
                }
                case InformGiveCardResp informGiveCardResp -> {
                    if (informGiveCardResp.getTargetPlayerId() == playerSession.getPlayer().getId()) {
                        playerSession.getSession().getBasicRemote().sendText(message);
                    } else if (!originPlayer.equals(playerSession)) {
                        Resp hiddenInformGiveCardResp = new InformGiveCardResp(
                                ResponseStatus.OK,
                                informGiveCardResp.getOriginPlayerId(),
                                informGiveCardResp.getTargetPlayerId(),
                                null
                        );
                        playerSession.getSession().getBasicRemote().sendText(generator.generateMessage(hiddenInformGiveCardResp));
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + resp);
            }
        }
    }
}
