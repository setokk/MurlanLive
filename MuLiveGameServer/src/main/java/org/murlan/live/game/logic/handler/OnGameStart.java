package org.murlan.live.game.logic.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Hand;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.PassCounter;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGameStartResp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.GameStateDto;
import org.murlan.live.protocol.dto.Player;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class OnGameStart implements Consumer<GameState> {
    private final Room room;
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;
    private final ProtocolConfig config;
    private final ScheduledExecutorService scheduler;

    @Override
    public void accept(GameState gameState) {
        synchronized (room) {
            boolean loserContainsBothJokers = gameState.prevLoserContainsBothJokers();

            if (room.getTotalFinishedGames() == 0) {
                gameState.setState(GameState.State.PLAYING);
            } else {
                gameState.setState(loserContainsBothJokers ? GameState.State.PLAYING : GameState.State.GIVING_CARDS);
            }

            gameState.setCurrCardCombination(GameConstants.EMPTY_CARD_COMBINATION);
            gameState.setScheduler(scheduler);

            List<Player> players = gameState.getPlayers();
            List<Hand> hands = Shuffler.shuffle(players.size());
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setHand(hands.get(i));
            }

            if (room.getTotalFinishedGames() == 0) {
                gameState.setCurrTurnPlayer(gameState.findPlayerWithCardCombination(new CardCombination(Card.THREE_OF_SPADES)));
                gameState.setShouldCurrTurnPlayerUseThreeOfSpades(true);
            } else {
                gameState.setCurrTurnPlayer(loserContainsBothJokers ? gameState.getPrevWinner() : gameState.getPrevLoser());
                gameState.setShouldCurrTurnPlayerUseThreeOfSpades(false);
            }

            gameState.setPassCounter(new PassCounter(0));
            gameState.setFirstMove(true);

            GameStateDto gameStateDto = GameStateDto.from(gameState, room, config);
            try {
                endpointHelper.informPlayers(new InformGameStartResp(ResponseStatus.OK, gameStateDto), null, roomHandler.getPlayersInRoom(room.getId()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            gameState.startTurnTimer();
        }
    }
}
