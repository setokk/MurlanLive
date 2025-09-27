package org.murlan.live.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.game.logic.MovePipeline;
import org.murlan.live.session.player.PlayerDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        this.players = new ArrayList<>();
        this.players.add(player);
        this.score = new HashMap<>();
    }

    public synchronized boolean addPlayer(PlayerDto player) {
        if (players.size() == MAX_PLAYERS) {
            return false;
        }
        this.players.add(player);
        if (players.size() == MAX_PLAYERS) {
            startGame();
        }
        return true;
    }


    public synchronized void playHand(String jwt, CardCombination cardCombination) {
        if (this.state != State.PLAYING) {
            return;
        }
        if (isNotPlayerTurn(jwt)) {
            return;
        }
        if (!this.currTurnPlayer.getDeck().contains(cardCombination)) {
            return;
        }
        if (!MovePipeline.validate(cardCombination) || cardCombination.isWeakerThan(this.currCardCombination)) {
            return;
        }

        this.currTurnPlayer.getDeck().removeCards(cardCombination);
        this.currCardCombination = cardCombination;
        nextTurn();
    }

    public synchronized void pass(String jwt) {
        if (this.state != State.PLAYING) {
            return;
        }
        if (isNotPlayerTurn(jwt)) {
            return;
        }

        nextTurn();
    }

    public synchronized void surrender(String jwt) {
        if (this.state != State.PLAYING) {
            return;
        }
        Optional<PlayerDto> playerToSurrender = this.players.stream().filter(p -> p.getJwt().equals(jwt)).findFirst();
        if (playerToSurrender.isEmpty()) {
            return;
        }

        this.players.remove(playerToSurrender.get());
        this.score.put(playerToSurrender.get(), -1);
        if (this.players.isEmpty()) {
            this.state = State.FINISHED;
        }
    }

    private synchronized void startGame() {
        this.state = State.PLAYING;
        this.currTurnPlayer = players.get(new Random().nextInt(0, players.size()));

        // Shuffle and assign decks to each player
        List<Deck> decks = Shuffler.shuffle(players.size());
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setDeck(decks.get(i));
        }
    }

    private boolean isNotPlayerTurn(String jwt) {
        return !jwt.equals(this.currTurnPlayer.getJwt());
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
