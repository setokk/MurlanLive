package org.murlan.live.game.logic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Rank;
import org.murlan.live.protocol.dto.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class GameState {
    private State state;
    private List<Player> players;
    private Map<Player, Short> score;

    @JsonIgnore private Player currTurnPlayer;
    @JsonIgnore private boolean shouldCurrTurnPlayerUseThreeOfSpades;
    @JsonIgnore private CardCombination currCardCombination;
    @JsonIgnore private Consumer<GameState> onStartGame;
    @JsonIgnore private Runnable onFinishGame;
    @JsonIgnore private Consumer<GameState> onTurnTimeout;
    @JsonIgnore private Player prevWinner;
    @JsonIgnore private Player prevLoser;
    @JsonIgnore private Set<Player> givenCards = HashSet.newHashSet(0);

    /* Turn timer */
    @JsonIgnore private ScheduledExecutorService scheduler;
    @JsonIgnore private ScheduledFuture<?> turnTimer;

    public GameState(State state, Player player, Consumer<GameState> onStartGame, Runnable onFinishGame, Consumer<GameState> onTurnTimeout) {
        this.state = state;
        this.players = new ArrayList<>();
        this.players.add(player);
        this.score = new HashMap<>();
        this.onStartGame = onStartGame;
        this.onFinishGame = onFinishGame;
        this.onTurnTimeout = onTurnTimeout;
    }

    public static GameState fromPrevious(GameState previous, Player winner, Player loser) {
        return GameState.builder()
                .withState(State.WAITING)
                .withPlayers(new ArrayList<>(previous.getPlayers()))
                .withScore(HashMap.newHashMap(GameConstants.MAX_PLAYERS))
                .withOnStartGame(previous.getOnStartGame())
                .withOnFinishGame(previous.getOnFinishGame())
                .withOnTurnTimeout(previous.getOnTurnTimeout())
                .withPrevWinner(winner)
                .withPrevLoser(loser)
                .build();
    }

    @JsonIgnore
    public boolean isFromPrevious() {
        return prevLoser != null && prevWinner != null;
    }

    public boolean addPlayer(Player player) {
        if (players.size() == GameConstants.MAX_PLAYERS) {
            return false;
        }
        this.players.add(player);
        if (players.size() == GameConstants.MAX_PLAYERS) {
            startGame();
        }
        return true;
    }


    public boolean playHand(Player player, CardCombination cardCombination) {
        if (this.state != State.PLAYING) {
            return false;
        }
        if (isNotPlayerTurn(player)) {
            return false;
        }
        if (!this.currTurnPlayer.getHand().contains(cardCombination)) {
            return false;
        }
        if (!MovePipeline.validate(cardCombination)) {
            return false;
        }

        boolean isFirstMove = this.currCardCombination == GameConstants.EMPTY_CARD_COMBINATION;
        if (isFirstMove && shouldCurrTurnPlayerUseThreeOfSpades && !cardCombination.getCards().contains(Card.THREE_OF_SPADES)) {
            return false;
        }

        if (!isFirstMove && (this.currCardCombination.isEqualStrength(cardCombination) || this.currCardCombination.isStrongerThan(cardCombination))) {
            return false;
        }

        this.currTurnPlayer.getHand().removeCards(cardCombination);
        this.currCardCombination = cardCombination;
        if (this.currTurnPlayer.getHand().isEmpty()) {
            this.score.put(this.currTurnPlayer, (short) (GameConstants.MAX_PLAYERS - this.score.size() - 1));
        }
        nextTurn();

        return true;
    }

    public boolean pass(Player player) {
        if (this.state != State.PLAYING) {
            return false;
        }
        if (isNotPlayerTurn(player)) {
            return false;
        }
        nextTurn();

        return true;
    }

    public boolean giveCard(Card card, Player player, Player receivingPlayer) {
        if (this.state != State.GIVING_CARDS) {
            return false;
        }
        if (!player.getHand().contains(new CardCombination(card))) {
            return false;
        }
        if (givenCards.contains(player)) { // player has already given a card
            return false;
        }

        Player actualReceivingPlayer = this.players.stream()
                .filter(receivingPlayer::equals)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Receiving player not found"));

        if (prevWinner.equals(player) && card.hasBiggerRank(Rank.TEN)) {
            return false;
        } else if (prevLoser.equals(player)) {
            Rank highestRank = player.getHand().getCards()
                    .stream()
                    .max(new Card.CardComparator())
                    .orElseThrow(() -> new IllegalStateException("No max card found"))
                    .rank();
            if (!highestRank.equals(card.rank())) {
                return false;
            }
        } else {
            return false;
        }

        player.getHand().removeCard(card);
        actualReceivingPlayer.getHand().addCard(card);

        givenCards.add(player);
        if (haveBothPlayersGivenCards()) {
            this.state = State.PLAYING;
            cancelTurnTimer();
        }

        return true;
    }

    public void handlePlayerNotInRoom(Player player, boolean hasLostConnection) {
        Optional<Player> optionalPlayer = this.players.stream().filter(player::equals).findAny();
        if (optionalPlayer.isEmpty()) {
            return;
        }

        if (state == State.PLAYING) {
            short scorePenalty = hasLostConnection
                    ? GameConstants.SCORE_PENALTY_LOST_CONNECTION
                    : GameConstants.SCORE_PENALTY_LEAVE_ROOM;

            short scoreRemainingPlayers = hasLostConnection
                    ? GameConstants.SCORE_REMAINING_PLAYERS_AFTER_LOST_CONNECTION
                    : GameConstants.SCORE_REMAINING_PLAYERS_AFTER_LEAVE_ROOM;

            for (Player remainingPlayer : this.players) {
                this.score.put(remainingPlayer, scoreRemainingPlayers);
            }
            this.score.put(optionalPlayer.get(), scorePenalty);
        }
    }

    public void startGame() {
        if (!State.WAITING.equals(this.state) && !State.GIVING_CARDS.equals(this.state)) {
            return;
        }
        onStartGame.accept(this);
    }

    private void finishGame() {
        if (State.FINISHED.equals(this.state)) {
            return;
        }

        cancelTurnTimer();

        this.state = State.FINISHED;
        onFinishGame.run();
    }

    private void nextTurn() {
        int nextTurnIndex = (players.indexOf(currTurnPlayer) + 1) % players.size();
        this.currTurnPlayer = players.get(nextTurnIndex);
        while (this.currTurnPlayer.getHand().isEmpty()) {
            nextTurnIndex = (nextTurnIndex + 1) % players.size();
            this.currTurnPlayer = players.get(nextTurnIndex);
        }
        if (this.score.size() == GameConstants.MAX_PLAYERS - 1) {
            this.score.put(this.currTurnPlayer, (short) 0);
            finishGame();
        } else {
            startTurnTimer();
        }
    }

    private boolean isNotPlayerTurn(Player player) {
        return !player.equals(this.currTurnPlayer);
    }

    public Player findPlayerWithCardCombination(CardCombination cardCombination) {
        return this.players.stream()
                .filter(p -> p.getHand().contains(cardCombination))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No player found with card combination " + cardCombination));
    }

    public boolean prevLoserContainsBothJokers() {
        return isFromPrevious() && getPrevLoser().getHand().contains(new CardCombination(Card.BLACK_JOKER, Card.RED_JOKER));
    }

    public boolean haveBothPlayersGivenCards() {
        return getGivenCards().size() == 2;
    }

    public void startTurnTimer() {
        cancelTurnTimer();

        turnTimer = scheduler.schedule(
                () -> onTurnTimeout.accept(this),
                GameConstants.TURN_DURATION_SECONDS,
                TimeUnit.SECONDS
        );
    }

    private void cancelTurnTimer() {
        if (turnTimer != null) {
            turnTimer.cancel(false);
            turnTimer = null;
        }
    }

    public Map<Long, Short> getNumOfCardsPerPlayerId() {
        return players.stream()
                .collect(Collectors.toMap(Player::getId, p -> (short) p.getHand().size()));
    }

    public enum State {
        WAITING,
        GIVING_CARDS,
        PLAYING,
        FINISHED
    }
}
