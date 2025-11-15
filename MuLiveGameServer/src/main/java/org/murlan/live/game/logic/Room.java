package org.murlan.live.game.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.dto.PlayerDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@RequiredArgsConstructor
public class Room {
    private final String id;
    private final String name;
    private final boolean isPublic;
    private final String passcode;
    private final LocalDateTime creationDate;
    private final short totalScoreToWin;
    private List<GameState> gameStates;
    private final PlayerDto owner;
    private final GameStateFactory gameStateFactory;

    public void newGameState() {
        this.gameStates = List.of(gameStateFactory.createGameState(this));
    }

    public boolean addPlayer(PlayerDto player) {
        return getActiveGameState().addPlayer(player);
    }

    public void startNewGameFromPreviousGame(PlayerDto winner, PlayerDto loser) {
        GameState prevGameState = getActiveGameState();
        if (GameState.State.FINISHED.equals(prevGameState.getState())) {
            this.getGameStates().add(GameState.fromPrevious(prevGameState, winner, loser));
        }
    }

    public GameState getActiveGameState() {
        return gameStates.getLast();
    }

    public int getTotalPlayedGames() {
        return gameStates.size() - 1;
    }

    public Map<PlayerDto, Short> getTotalScores() {
        return gameStates.stream()
                .map(GameState::getScore)
                .flatMap(s -> s.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (partialScore, currScore) -> (short) (partialScore + currScore)
                ));
    }

    public int getNumPlayers() {
        return getActiveGameState().getPlayers().size();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Room room)) return false;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
