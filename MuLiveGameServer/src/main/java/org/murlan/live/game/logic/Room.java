package org.murlan.live.game.logic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.protocol.dto.Player;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class Room {
    private UUID id;
    private String name;
    private final boolean isPublic;
    private final LocalDateTime creationDate;
    private short totalScoreToWin;
    private List<GameState> gameStates;
    private Map<Player, Short> exitPenalties;
    private Player owner;
    private long turnDurationInSeconds;
    private final GameStateFactory gameStateFactory;

    public Room(String name,
                boolean isPublic,
                LocalDateTime creationDate,
                short totalScoreToWin,
                Player owner,
                long turnDurationInSeconds,
                GameStateFactory gameStateFactory) {
        this.name = name;
        this.isPublic = isPublic;
        this.creationDate = creationDate;
        this.totalScoreToWin = totalScoreToWin;
        this.owner = owner;
        this.turnDurationInSeconds = turnDurationInSeconds;
        this.gameStateFactory = gameStateFactory;
    }

    public void initialGameState() {
        this.gameStates = new ArrayList<>();
        this.gameStates.add(gameStateFactory.createGameState(this));
    }

    public synchronized boolean addPlayer(Player player, Runnable onSuccess) {
        return getActiveGameState().addPlayer(player, onSuccess);
    }

    public synchronized void startNewGameFromPreviousGame(Player winner, Player loser) {
        GameState prevGameState = getActiveGameState();
        if (GameState.State.FINISHED.equals(prevGameState.getState())) {
            gameStates.add(GameState.fromPrevious(prevGameState, winner, loser));
            getActiveGameState().startGame();
        }
    }

    public synchronized GameState getActiveGameState() {
        return gameStates.getLast();
    }

    public synchronized int getTotalFinishedGames() {
        if (GameState.State.FINISHED.equals(getActiveGameState().getState())) {
            return gameStates.size();
        } else {
            return Math.max(0, gameStates.size() - 1);
        }
    }

    public synchronized Map<Player, Short> getTotalScores() {
        Map<Player, Integer> totals = new LinkedHashMap<>();
        Map<Player, Integer> reachedAt = new HashMap<>();
        int seq = 0;

        for (GameState gs : gameStates) {
            // players without a score entry yet still need to appear
            for (Player p : gs.getPlayers()) {
                if (!totals.containsKey(p)) {
                    totals.put(p, 0);
                    reachedAt.put(p, seq++);
                }
            }

            for (Map.Entry<Player, Short> e : gs.getScore().entrySet()) {
                Player p = e.getKey();
                int before = totals.get(p);
                int after = before + e.getValue();
                totals.put(p, after);

                if (after != before) {
                    reachedAt.put(p, seq);
                }
                seq++;
            }
        }

        // penalties for players who left/disconnected mid-game, whose interrupted game state was discarded
        if (exitPenalties != null) {
            for (Map.Entry<Player, Short> e : exitPenalties.entrySet()) {
                Player p = e.getKey();
                totals.putIfAbsent(p, 0);
                reachedAt.putIfAbsent(p, seq);
                totals.put(p, totals.get(p) + e.getValue());
                reachedAt.put(p, seq);
                seq++;
            }
        }

        Map<Player, Short> result = new LinkedHashMap<>();
        totals.entrySet().stream()
                .sorted(Comparator
                        .comparing((Map.Entry<Player, Integer> e) -> e.getValue()).reversed()
                        .thenComparing(e -> reachedAt.get(e.getKey())))
                .forEach(e -> result.put(e.getKey(), e.getValue().shortValue()));

        return result;
    }

    public synchronized List<Player> getPlayers() {
        return getActiveGameState().getPlayers();
    }

    public synchronized void applyExitPenalty(Player player, short penalty) {
        if (exitPenalties == null) {
            exitPenalties = new LinkedHashMap<>();
        }
        exitPenalties.put(player, penalty);
    }

    public synchronized boolean discardActiveGameStateForInterruption() {
        if (gameStates.size() <= 1) {
            return false;
        }
        gameStates.removeLast();
        return true;
    }

    public void finishDueToPlayerExit() {
        try {
            getActiveGameState().getOnGameFinish().runForPlayerExit();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public synchronized boolean ready(Player player) {
        return getActiveGameState().ready(player);
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
