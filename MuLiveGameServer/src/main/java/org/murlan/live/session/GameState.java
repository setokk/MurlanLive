package org.murlan.live.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.game.logic.MovePipeline;
import org.murlan.live.session.player.PlayerDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class GameState {
    public static final int MAX_PLAYERS = 4;

    private PlayerDto currTurnPlayer;
    private State state;
    private List<PlayerDto> players;
    private Map<PlayerDto, Integer> score;
    private CardCombination currCardCombination;

    public GameState(State state, PlayerDto player) {
        this.state = state;
        this.players = Arrays.asList(player);
        this.score = new HashMap<>();
    }

    public synchronized void addPlayer(PlayerDto player) {
        if (players.size() == MAX_PLAYERS) {
            return;
        }
        this.players.add(player);
        if (players.size() == MAX_PLAYERS) {
            startGame();
        }
    }

    public synchronized boolean playHand(String jwt, CardCombination cardCombination) {
        boolean isPlayerTurn = isPlayerTurn(jwt);
        if (!isPlayerTurn) {
            return false;
        }

        boolean isValidMove = MovePipeline.validateMove(cardCombination);
        if (!isValidMove || currCardCombination.isStrongerThan(cardCombination)) {
            return false;
        }
        this.currCardCombination = cardCombination;

        nextTurn();
        return true;
    }

    public synchronized void pass(String jwt) {
        nextTurn();
    }

    public synchronized void surrender() {
        this.players.remove(currTurnPlayer);
        this.score.put(currTurnPlayer, -1);
        if (players.isEmpty()) {
            this.state = State.FINISHED;
        }
    }

    private void startGame() {
        this.state = State.PLAYING; // Set game state to PLAYING
        this.currTurnPlayer = players.get(new Random().nextInt(0, players.size())); // Randomly select which player is going to start

        // Shuffle and assign decks to each player
        List<Deck> decks = Shuffler.shuffle(players.size());
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setDeck(decks.get(i));
        }
    }

    private boolean isPlayerTurn(String jwt) {
        return jwt.equals(this.currTurnPlayer.getJwt());
    }

    private void nextTurn() {
        int nextTurnIndex = (players.indexOf(currTurnPlayer) + 1) % players.size();
        this.currTurnPlayer = players.get(nextTurnIndex);
    }

    public enum State {
        WAITING,
        PLAYING,
        FINISHED
    }
}
