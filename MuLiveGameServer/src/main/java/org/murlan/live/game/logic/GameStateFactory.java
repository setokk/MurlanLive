package org.murlan.live.game.logic;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Hand;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGameFinishResp;
import org.murlan.live.protocol.api.InformGameStartResp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.GameFinishDto;
import org.murlan.live.protocol.dto.GameStateDto;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.rest.RoomRESTClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameStateFactory {
    private final RoomRESTClient roomRESTClient;
    private final EndpointHelper endpointHelper;
    private final RoomHandler roomHandler;
    private final ProtocolConfig config;

    public GameState createGameState(Room room) {
        Consumer<GameState> onStartGame = (gameState) -> {
            boolean loserContainsBothJokers = gameState.prevLoserContainsBothJokers();
            gameState.setState(loserContainsBothJokers ? GameState.State.GIVING_CARDS : GameState.State.PLAYING);
            gameState.setCurrCardCombination(GameConstants.EMPTY_CARD_COMBINATION);

            List<Player> players = gameState.getPlayers();
            List<Hand> hands = Shuffler.shuffle(players.size());
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setHand(hands.get(i));
            }

            if (room.getTotalFinishedGames() == 0) {
                gameState.setCurrTurnPlayer(gameState.findPlayerWithCardCombination(new CardCombination(Card.THREE_OF_SPADES)));
                gameState.setShouldCurrTurnPlayerUseThreeOfSpades(true);
            } else {
                gameState.setCurrTurnPlayer(gameState.prevLoserContainsBothJokers() ? gameState.getPrevWinner() : gameState.getPrevLoser());
                gameState.setShouldCurrTurnPlayerUseThreeOfSpades(false);
            }

            GameStateDto gameStateDto = GameStateDto.from(gameState, room, config);
            try {
                endpointHelper.informPlayers(new InformGameStartResp(ResponseStatus.OK, gameStateDto), null, roomHandler.getPlayersInRoom(room.getId()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Runnable onFinishGame = () -> {
            Map<Player, Short> previousScore = room.getActiveGameState().getScore();
            Player winner = previousScore.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new IllegalStateException(""))
                    .getKey();
            Player loser = previousScore.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .orElseThrow(() -> new IllegalStateException(""))
                    .getKey();

            Optional<Player> optionalFinalWinner = room.getTotalScores().entrySet().stream()
                    .filter(s -> s.getValue() >= room.getTotalScoreToWin())
                    .map(Map.Entry::getKey)
                    .findAny();
            if (optionalFinalWinner.isPresent()) {
                try {
                    roomRESTClient.createRoom(room);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                room.startNewGameFromPreviousGame(winner, loser);
            }

            try {
                GameFinishDto gameFinishDto = GameFinishDto.builder()
                        .winnerPlayerId(winner.getId())
                        .loserPlayerId(loser.getId())
                        .scorePerPlayerId(previousScore.entrySet().stream()
                                .collect(Collectors.toMap(
                                        entry -> entry.getKey().getId(),
                                        Map.Entry::getValue)
                                )
                        )
                        .build();
                endpointHelper.informPlayers(new InformGameFinishResp(ResponseStatus.OK, gameFinishDto), null, roomHandler.getPlayersInRoom(room.getId()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        return new GameState(GameState.State.WAITING, room.getOwner(), onStartGame, onFinishGame);
    }
}
