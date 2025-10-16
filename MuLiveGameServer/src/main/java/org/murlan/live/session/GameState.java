package org.murlan.live.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.game.logic.MovePipeline;
import org.murlan.live.protocol.dto.PlayerDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class GameState {
    public static final int MAX_PLAYERS = 4;
    private static final CardCombination EMPTY_CARD_COMBINATION = new CardCombination();

    private PlayerDto currTurnPlayer;
    private State state;
    private List<PlayerDto> players;
    private Map<PlayerDto, Short> score;
    private CardCombination currCardCombination;
    private Runnable onFinishGame;

    public GameState(State state, PlayerDto player, Runnable onFinishGame) {
        this.state = state;
        this.players = new ArrayList<>();
        this.players.add(player);
        this.score = new HashMap<>();
        this.onFinishGame = onFinishGame;
    }

    public GameState(State state, List<PlayerDto> players, Runnable onFinishGame) {
        this.state = state;
        this.players = players;
        this.score = new HashMap<>();
        this.onFinishGame = onFinishGame;
    }

    public static GameState createFromPrevious(GameState previous) {
        GameState newGameState = new GameState(State.WAITING, previous.getPlayers(), previous.getOnFinishGame());
        newGameState.startGame();
        return newGameState;
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


    public synchronized boolean playHand(PlayerDto player, CardCombination cardCombination) {
        if (this.state != State.PLAYING) {
            return false;
        }
        if (isNotPlayerTurn(player)) {
            return false;
        }
        if (!this.currTurnPlayer.getDeck().contains(cardCombination)) {
            return false;
        }
        if (!MovePipeline.validate(cardCombination)) {
            return false;
        }
        boolean isNotFirstMove = this.currCardCombination != EMPTY_CARD_COMBINATION;
        if (isNotFirstMove && (this.currCardCombination.isEqualStrength(cardCombination) || this.currCardCombination.isStrongerThan(cardCombination))) {
            return false;
        }

        this.currTurnPlayer.getDeck().removeCards(cardCombination);
        this.currCardCombination = cardCombination;
        if (this.currTurnPlayer.getDeck().isEmpty()) {
            this.score.put(this.currTurnPlayer, (short) (MAX_PLAYERS - this.score.size() - 1));
        }
        nextTurn();

        return true;
    }

    public synchronized boolean pass(PlayerDto player) {
        if (this.state != State.PLAYING) {
            return false;
        }
        if (isNotPlayerTurn(player)) {
            return false;
        }
        nextTurn();

        return true;
    }

    public synchronized boolean surrender(PlayerDto player) {
        if (this.state != State.PLAYING) {
            return false;
        }
        Optional<PlayerDto> optionalPlayerToSurrender = this.players.stream().filter(player::equals).findAny();
        if (optionalPlayerToSurrender.isEmpty()) {
            return false;
        }

        PlayerDto playerToSurrender = optionalPlayerToSurrender.get();
        this.players.remove(playerToSurrender);
        this.score.put(playerToSurrender, (short)-1);
        for (PlayerDto remainingPlayer : this.players) {
            this.score.put(remainingPlayer, (short)1);
        }
        finishGame();

        return true;
    }

    // TODO: More logic to take into account (first game of room and later on the other ones)
    public synchronized void startGame() {
        this.state = State.PLAYING;
        this.currCardCombination = EMPTY_CARD_COMBINATION;

        // Shuffle and assign decks to each player
        List<Deck> decks = Shuffler.shuffle(players.size());
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setDeck(decks.get(i));
        }
        this.currTurnPlayer = findPlayerWithCardCombination(new CardCombination(Card.THREE_OF_SPADES));
    }

    private void nextTurn() {
        int nextTurnIndex = (players.indexOf(currTurnPlayer) + 1) % players.size();
        this.currTurnPlayer = players.get(nextTurnIndex);
        while (this.currTurnPlayer.getDeck().isEmpty()) {
            nextTurnIndex = (nextTurnIndex + 1) % players.size();
            this.currTurnPlayer = players.get(nextTurnIndex);
        }
        if (this.score.size() == MAX_PLAYERS - 1) {
            this.score.put(this.currTurnPlayer, (short)0);
            finishGame();
        }
    }

    private boolean isNotPlayerTurn(PlayerDto player) {
        return !player.equals(this.currTurnPlayer);
    }

    private void finishGame() {
        if (State.FINISHED.equals(this.state)) {
            return;
        }
        this.state = State.FINISHED;
        if (players.size() != MAX_PLAYERS) {
            return; // Surrender has occurred
        }
        onFinishGame.run();
    }

    private PlayerDto findPlayerWithCardCombination(CardCombination cardCombination) {
        return this.players.stream()
                .filter(p -> p.getDeck().contains(cardCombination))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No player found with card combination " + cardCombination));
    }

    public enum State {
        WAITING,
        PLAYING,
        FINISHED
    }
}
