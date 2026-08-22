package org.murlan.live.game.logic;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.PlayerSession;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Hand;
import org.murlan.live.game.deck.Rank;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGameFinishResp;
import org.murlan.live.protocol.api.InformGameStartResp;
import org.murlan.live.protocol.api.InformGiveCardResp;
import org.murlan.live.protocol.api.InformPassResp;
import org.murlan.live.protocol.config.ProtocolConfig;
import org.murlan.live.protocol.dto.GameFinishDto;
import org.murlan.live.protocol.dto.GameStateDto;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.rest.RoomRESTClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameStateFactory {
    private final RoomRESTClient roomRESTClient;
    private final EndpointHelper endpointHelper;
    private final RoomHandler roomHandler;
    private final ProtocolConfig config;
    private final ScheduledExecutorService scheduler;

    public GameState createGameState(Room room) {
        Consumer<GameState> onStartGame = (gameState) -> {
            synchronized (room) {
                boolean loserContainsBothJokers = gameState.prevLoserContainsBothJokers();
                gameState.setState(loserContainsBothJokers ? GameState.State.GIVING_CARDS : GameState.State.PLAYING);
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
                    gameState.setCurrTurnPlayer(gameState.prevLoserContainsBothJokers() ? gameState.getPrevWinner() : gameState.getPrevLoser());
                    gameState.setShouldCurrTurnPlayerUseThreeOfSpades(false);
                }

                GameStateDto gameStateDto = GameStateDto.from(gameState, room, config);
                try {
                    endpointHelper.informPlayers(new InformGameStartResp(ResponseStatus.OK, gameStateDto), null, roomHandler.getPlayersInRoom(room.getId()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                gameState.startTurnTimer();
            }
        };

        Runnable onFinishGame = () -> {
            synchronized (room) {
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
            }
        };

        Consumer<GameState> onTurnTimeout = (gameState) -> {
            synchronized (room) {
                if (gameState.getState() == GameState.State.PLAYING) {
                    try {
                        Player currTurnPlayer = gameState.getCurrTurnPlayer();
                        gameState.pass(currTurnPlayer);
                        endpointHelper.informPlayers(new InformPassResp(ResponseStatus.OK, currTurnPlayer.getId()), null, roomHandler.getPlayersInRoom(room.getId()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (gameState.getState() == GameState.State.GIVING_CARDS) {
                    InformGiveCardResp informGiveCardResp = new InformGiveCardResp(ResponseStatus.OK,
                            -1, -1, null,
                            room.getActiveGameState().haveBothPlayersGivenCards()
                    );

                    if (!gameState.getGivenCards().contains(gameState.getPrevLoser())) {
                        Card highestCard = gameState.getPrevLoser().getHand().getCards()
                                .stream()
                                .max(new Card.CardComparator())
                                .orElseThrow(() -> new IllegalStateException("No max card found"));

                        gameState.giveCard(highestCard, gameState.getPrevLoser(), gameState.getPrevWinner());

                        updateAndInformPlayersForGiveCard(informGiveCardResp,
                                roomHandler.getPlayersInRoom(room.getId()),
                                gameState.getPrevLoser(), gameState.getPrevWinner(),
                                highestCard);
                    }

                    if (!gameState.getGivenCards().contains(gameState.getPrevWinner())) {
                        Card randomCardLessOrEqualThan10 = gameState.getPrevWinner().getHand().getCards()
                                .stream()
                                .filter(c -> c.hasSmallerOrEqualRank(Rank.TEN))
                                .collect(Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        cards -> {
                                            if (cards.isEmpty()) {
                                                throw new IllegalStateException("No card with rank <= 10 found");
                                            }
                                            return cards.get(ThreadLocalRandom.current().nextInt(cards.size()));
                                        }
                                ));

                        gameState.giveCard(randomCardLessOrEqualThan10, gameState.getPrevWinner(), gameState.getPrevLoser());

                        updateAndInformPlayersForGiveCard(informGiveCardResp,
                                roomHandler.getPlayersInRoom(room.getId()),
                                gameState.getPrevWinner(), gameState.getPrevLoser(),
                                randomCardLessOrEqualThan10);
                    }
                }
            }
        };

        return new GameState(GameState.State.WAITING, room.getOwner(), onStartGame, onFinishGame, onTurnTimeout);
    }

    private void updateAndInformPlayersForGiveCard(InformGiveCardResp informGiveCardResp,
                                                   List<PlayerSession> playersInRoom,
                                                   Player originPlayer, Player targetPlayer,
                                                   Card card) {
        informGiveCardResp.setOriginPlayerId(originPlayer.getId());
        informGiveCardResp.setTargetPlayerId(targetPlayer.getId());
        informGiveCardResp.setCard(card);

        PlayerSession originPlayerSession = playersInRoom.stream()
                .filter(ps -> ps.getPlayer().equals(originPlayer))
                .findAny()
                .orElseThrow();
        try {
            endpointHelper.informPlayers(informGiveCardResp, originPlayerSession, playersInRoom);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
