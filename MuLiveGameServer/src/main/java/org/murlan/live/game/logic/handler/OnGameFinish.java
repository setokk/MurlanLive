package org.murlan.live.game.logic.handler;

import lombok.RequiredArgsConstructor;
import org.murlan.live.endpoint.EndpointHelper;
import org.murlan.live.endpoint.session.RoomHandler;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.ResponseStatus;
import org.murlan.live.protocol.api.InformGameFinishResp;
import org.murlan.live.protocol.dto.GameFinishDto;
import org.murlan.live.protocol.dto.Player;
import org.murlan.live.protocol.rest.RoomRESTClient;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class OnGameFinish implements Runnable {
    private final Room room;
    private final RoomHandler roomHandler;
    private final EndpointHelper endpointHelper;
    private final RoomRESTClient roomRESTClient;

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

            try {
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
                        .build();
                endpointHelper.informPlayers(new InformGameFinishResp(ResponseStatus.OK, gameFinishDto), null, roomHandler.getPlayersInRoom(room.getId()));

                if (isFinalWinner) {
                    try {
                        roomRESTClient.createRoom(room);
                        roomHandler.copyRoom(room.getId());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    room.startNewGameFromPreviousGame(winner, loser);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
