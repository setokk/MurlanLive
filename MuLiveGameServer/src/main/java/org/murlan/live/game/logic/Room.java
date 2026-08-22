package org.murlan.live.game.logic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.protocol.dto.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private String id;
    private final String name;
    @JsonProperty("isPublic") private final boolean isPublic;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") private final LocalDateTime creationDate;
    private final short totalScoreToWin;
    private List<GameState> gameStates;
    private final Player owner;
    @JsonIgnore private final GameStateFactory gameStateFactory;

    public void initialGameState() {
        this.gameStates = new ArrayList<>();
        this.gameStates.add(gameStateFactory.createGameState(this));
    }

    public synchronized boolean addPlayer(Player player) {
        return getActiveGameState().addPlayer(player);
    }

    public synchronized void startNewGameFromPreviousGame(Player winner, Player loser) {
        GameState prevGameState = getActiveGameState();
        if (GameState.State.FINISHED.equals(prevGameState.getState())) {
            gameStates.add(GameState.fromPrevious(prevGameState, winner, loser));
            getActiveGameState().startGame();
        }
    }

    @JsonIgnore
    public synchronized GameState getActiveGameState() {
        return gameStates.getLast();
    }

    @JsonIgnore
    public synchronized int getTotalFinishedGames() {
        if (GameState.State.FINISHED.equals(getActiveGameState().getState())) {
            return gameStates.size();
        } else {
            return Math.max(0, gameStates.size() - 1);
        }
    }

    public synchronized Map<Player, Short> getTotalScores() {
        return gameStates.stream()
                .map(GameState::getScore)
                .flatMap(s -> s.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (partialScore, currScore) -> (short) (partialScore + currScore)
                ));
    }

    public synchronized List<Player> getPlayers() {
        return getActiveGameState().getPlayers();
    }

    public synchronized boolean playHand(Player player, CardCombination cardCombination) {
        return getActiveGameState().playHand(player, cardCombination);
    }

    public synchronized boolean pass(Player player) {
        return getActiveGameState().pass(player);
    }

    public synchronized boolean giveCard(Card card, Player player, Player receivingPlayer) {
        return getActiveGameState().giveCard(card, player, receivingPlayer);
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
