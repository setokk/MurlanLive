package org.murlan.live.game.logic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.protocol.dto.PlayerDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
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
        GameState gameState = gameStateFactory.createGameState(this);
        this.gameStates = List.of(gameState);
    }

    public boolean addPlayer(PlayerDto player) {
        return getActiveGameState().addPlayer(player);
    }

    public void startNewGameFromPreviousGame() {
        GameState lastGameState = getActiveGameState();
        if (GameState.State.FINISHED.equals(lastGameState.getState())) {
            this.gameStates.add(GameState.fromPrevious(lastGameState));
        }
    }

    public GameState getActiveGameState() {
        return gameStates.getLast();
    }

    public int getTotalGames() {
        return gameStates.size();
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
