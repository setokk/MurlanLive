package org.murlan.live.game.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.murlan.live.protocol.dto.Player;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link Room#getTotalScores()}.
 * <br/>
 * Scoring convention used in the tests (3 players): in a finished game the entries are put in
 * finish order with 2, 1, 0 points. Interrupted games contain only the leaver with a penalty.
 */
public class RoomTest {

    private Player p1;
    private Player p2;
    private Player p3;

    @BeforeEach
    void setUp() {
        p1 = player(1);
        p2 = player(2);
        p3 = player(3);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * TODO: adapt to however Player is constructed in your project.
     * Player must have equals/hashCode based on id for the maps to behave correctly.
     */
    private static Player player(long id) {
        return new Player(id, "p" + id, LocalDateTime.now(), "jwt");
    }

    private static Map.Entry<Player, Short> pts(Player p, int points) {
        return new AbstractMap.SimpleEntry<>(p, (short) points);
    }

    /** Entries must be given in finish order, they are stored in a LinkedHashMap. */
    @SafeVarargs
    private GameState game(Map.Entry<Player, Short>... entriesInFinishOrder) {
        Map<Player, Short> score = new LinkedHashMap<>();
        for (Map.Entry<Player, Short> e : entriesInFinishOrder) {
            score.put(e.getKey(), e.getValue());
        }
        return GameState.builder()
                .withState(GameState.State.FINISHED)
                .withPlayers(new ArrayList<>(List.of(p1, p2, p3)))
                .withScore(score)
                .build();
    }

    private Room room(GameState... games) {
        return Room.builder()
                .withTotalScoreToWin((short) 20)
                .withGameStates(new ArrayList<>(List.of(games)))
                .build();
    }

    private static void assertRanking(Room room, Object... playerAndTotalPairs) {
        Map<Player, Short> result = room.getTotalScores();

        List<Player> expectedPlayers = new ArrayList<>();
        List<Short> expectedTotals = new ArrayList<>();
        for (int i = 0; i < playerAndTotalPairs.length; i += 2) {
            expectedPlayers.add((Player) playerAndTotalPairs[i]);
            expectedTotals.add((short) (int) (Integer) playerAndTotalPairs[i + 1]);
        }

        assertEquals(expectedPlayers, new ArrayList<>(result.keySet()), "ranking order");
        assertEquals(expectedTotals, new ArrayList<>(result.values()), "totals in ranking order");
    }

    // ------------------------------------------------------------------
    // Basic cases
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Single finished game: ranking follows finish order")
    void singleGame() {
        Room room = room(game(pts(p1, 2), pts(p2, 1), pts(p3, 0)));

        assertRanking(room, p1, 2, p2, 1, p3, 0);
    }

    @Test
    @DisplayName("Single finished game: a different finish order gives a different ranking")
    void singleGameDifferentOrder() {
        Room room = room(game(pts(p3, 2), pts(p1, 1), pts(p2, 0)));

        assertRanking(room, p3, 2, p1, 1, p2, 0);
    }

    @Test
    @DisplayName("Game that has not produced any score yet: everyone has 0, in players order")
    void noScoresYet() {
        Room room = room(game());

        assertRanking(room, p1, 0, p2, 0, p3, 0);
    }

    @Test
    @DisplayName("Multiple games: higher cumulative total wins")
    void higherTotalWins() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p2, 2), pts(p3, 1), pts(p1, 0)));

        // p1=2, p2=3, p3=1
        assertRanking(room, p2, 3, p1, 2, p3, 1);
    }

    // ------------------------------------------------------------------
    // Tie breakers
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Tie for 1st: whoever reached the total first (finished first in the deciding game) ranks first")
    void tieForFirst() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p2, 2), pts(p1, 1), pts(p3, 0)));

        // p1=3, p2=3 ; p2 got to 3 first inside game 2
        assertRanking(room, p2, 3, p1, 3, p3, 0);
    }

    @Test
    @DisplayName("Tie for 1st: reversed deciding game flips the winner")
    void tieForFirstReversed() {
        Room room = room(
                game(pts(p2, 2), pts(p1, 1), pts(p3, 0)),
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)));

        // p1=3, p2=3 ; p1 got to 3 first inside game 2
        assertRanking(room, p1, 3, p2, 3, p3, 0);
    }

    @Test
    @DisplayName("Tie for 2nd/3rd: the one who reached the total earlier ranks higher (p2 earlier)")
    void tieForSecondAndThird_firstReachedEarlier() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p1, 2), pts(p3, 1), pts(p2, 0)));

        // p1=4, p2=1 (reached in game 1), p3=1 (reached in game 2)
        assertRanking(room, p1, 4, p2, 1, p3, 1);
    }

    @Test
    @DisplayName("Tie for 2nd/3rd: the one who reached the total earlier ranks higher (p3 earlier)")
    void tieForSecondAndThird_secondReachedEarlier() {
        Room room = room(
                game(pts(p1, 2), pts(p3, 1), pts(p2, 0)),
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)));

        // p1=4, p3=1 (reached in game 1), p2=1 (reached in game 2)
        assertRanking(room, p1, 4, p3, 1, p2, 1);
    }

    @Test
    @DisplayName("Tie for 1st across games with a third player behind, extra zero-point game changes nothing")
    void tieForFirstWithThirdBehind() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p3, 1), pts(p2, 1), pts(p1, 0)),
                game(pts(p2, 0), pts(p3, 0), pts(p1, 0)));

        // p1=2 (reached in game 1), p2=2 (reached in game 2), p3=1
        assertRanking(room, p1, 2, p2, 2, p3, 1);
    }

    @Test
    @DisplayName("Three-way tie at 2 points: order is the order in which each player got to 2")
    void threeWayTieAtSameTotal() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p3, 2), pts(p2, 1), pts(p1, 0)));

        // p1=2 (game 1), p2=2 (game 2, second), p3=2 (game 2, first)
        assertRanking(room, p1, 2, p3, 2, p2, 2);
    }

    @Test
    @DisplayName("A player scoring 0 later keeps the time they first reached their total")
    void zeroPointGameDoesNotMoveTiePosition() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p3, 1), pts(p2, 0), pts(p1, 0)),
                game(pts(p1, 0), pts(p2, 0), pts(p3, 0)));

        // p1=2, p2=1, p3=1. p2 reached 1 in game 1, p3 reached 1 in game 2 -> p2 above p3
        assertRanking(room, p1, 2, p2, 1, p3, 1);
    }

    // ------------------------------------------------------------------
    // Interruptions (leave / lost connection): the interrupted game is the last one
    // and only contains the leaver with the penalty.
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Interruption in the first game: all players still appear, leaver last")
    void interruptionInFirstGame() {
        Room room = room(game(pts(p1, -1)));

        // p1=-1, p2=0, p3=0 ; p2/p3 tied, fall back to players order
        assertRanking(room, p2, 0, p3, 0, p1, -1);
    }

    @Test
    @DisplayName("Interruption after earlier games: players keep their standing, leaver drops")
    void interruptionAfterEarlierGames() {
        Room room = room(
                game(pts(p3, 2), pts(p1, 1), pts(p2, 0)),
                game(pts(p3, -3)));

        // before: p3=2, p1=1, p2=0 ; after: p3=-1
        assertRanking(room, p1, 1, p2, 0, p3, -1);
    }

    @Test
    @DisplayName("Interruption: standing at the moment of interruption is kept for non-leavers")
    void interruptionKeepsStandingOfRemainingPlayers() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p3, -1)));

        // before: p1=4, p2=2, p3=0 ; after: p3=-1
        assertRanking(room, p1, 4, p2, 2, p3, -1);
    }

    @Test
    @DisplayName("Interruption: leaver tied with a remaining player ranks below them")
    void leaverTiedRanksBelow() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p1, -1)));

        // p1=1 (reached after the penalty), p2=1 (reached in game 1), p3=0
        assertRanking(room, p2, 1, p1, 1, p3, 0);
    }

    @Test
    @DisplayName("Interruption with a leader leaving but still ahead keeps first place")
    void leaderLeavesButStillAhead() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p1, 2), pts(p3, 1), pts(p2, 0)),
                game(pts(p1, -1)));

        // before: p1=6, p2=2, p3=1 ; after: p1=5
        assertRanking(room, p1, 5, p2, 2, p3, 1);
    }

    @Test
    @DisplayName("Interruption in a game where someone already finished: finisher keeps points, leaver gets penalty")
    void interruptionAfterAFinisherInSameGame() {
        // p2 finished first (2 points), then p1 left (penalty)
        Room room = room(game(pts(p2, 2), pts(p1, -1)));

        assertRanking(room, p2, 2, p3, 0, p1, -1);
    }

    // ------------------------------------------------------------------
    // Structure
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Result contains every player exactly once")
    void everyPlayerExactlyOnce() {
        Room room = room(
                game(pts(p1, 2), pts(p2, 1), pts(p3, 0)),
                game(pts(p2, 2), pts(p3, 1), pts(p1, 0)),
                game(pts(p3, 2), pts(p1, 1), pts(p2, 0)));

        Map<Player, Short> result = room.getTotalScores();

        assertEquals(3, result.size());
        assertEquals(List.of(p1, p2, p3), result.keySet().stream().sorted(java.util.Comparator.comparingLong(Player::getId)).toList());

        // all three end on 3 points. Order is who reached 3 first:
        // p2 in game 2, then p3 and p1 in game 3 (p3 finished before p1)
        assertRanking(room, p2, 3, p3, 3, p1, 3);
    }
}