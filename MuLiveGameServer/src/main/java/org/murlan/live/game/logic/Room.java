package org.murlan.live.game.logic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.murlan.live.protocol.dto.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder(setterPrefix = "with")
@RequiredArgsConstructor
@AllArgsConstructor
public class Room {
    private final String id;
    private final String name;
    @JsonProperty("isPublic") private final boolean isPublic;
    @JsonIgnore private final String passcode;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") private final LocalDateTime creationDate;
    private final short totalScoreToWin;
    private List<GameState> gameStates;
    private final Player owner;
    @JsonIgnore private final GameStateFactory gameStateFactory;

    public void newGameState() {
        this.gameStates = List.of(gameStateFactory.createGameState(this));
    }

    public boolean addPlayer(Player player) {
        return getActiveGameState().addPlayer(player);
    }

    public void startNewGameFromPreviousGame(Player winner, Player loser) {
        GameState prevGameState = getActiveGameState();
        if (GameState.State.FINISHED.equals(prevGameState.getState())) {
            this.getGameStates().add(GameState.fromPrevious(prevGameState, winner, loser));
        }
    }

    @JsonIgnore
    public synchronized GameState getActiveGameState() {
        return gameStates.getLast();
    }

    @JsonIgnore
    public int getTotalFinishedGames() {
        if (GameState.State.FINISHED.equals(getActiveGameState().getState())) {
            return gameStates.size();
        } else {
            return gameStates.size() - 1;
        }
    }

    public Map<Player, Short> getTotalScores() {
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
