package org.murlan.live.game.logic.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGameFinishResp;
import org.murlan.live.protocol.dto.GameFinishDto;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.dto.RoomDto;
import org.murlan.live.protocol.dto.um.UMRoomDto;
import org.murlan.live.protocol.rest.RoomRESTClient;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class OnGameFinish implements Runnable {
    private final Room room;
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;
    private final RoomRESTClient roomRESTClient;

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void run() {
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

            boolean isFinalWinner = optionalFinalWinner.isPresent();

            GameFinishDto gameFinishDto = GameFinishDto.builder()
                    .winnerPlayerId(winner.getId())
                    .loserPlayerId(loser.getId())
                    .scorePerPlayerId(previousScore.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> entry.getKey().getId(),
                                    Map.Entry::getValue)
                            )
                    )
                    .finalWinner(isFinalWinner ? optionalFinalWinner.get() : null)
                    .roomId(room.getId())
                    .build();

            if (isFinalWinner) {
                RoomDto copyRoomDto = roomHandler.copyRoom(room.getId());
                gameFinishDto.setRoomId(copyRoomDto.id());

                executor.execute(() -> {
                    try {
                        ResponseStatus responseStatus = ResponseStatus.OK;

                        Optional<UMRoomDto> optionalUmRoomDto = roomRESTClient.createRoom(room);
                        if (optionalUmRoomDto.isEmpty()) {
                            responseStatus = ResponseStatus.ERROR;
                        } else {
                            UMRoomDto umRoomDto = optionalUmRoomDto.get();
                            gameFinishDto.setRankRatingsByPlayerId(umRoomDto.getRankRatingsByPlayerId());
                        }
                        endpointHelper.informPlayers(new InformGameFinishResp(responseStatus, gameFinishDto), null, roomHandler.getPlayersInRoom(copyRoomDto.id()));
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                room.startNewGameFromPreviousGame(winner, loser);
            }
        }
    }
}
