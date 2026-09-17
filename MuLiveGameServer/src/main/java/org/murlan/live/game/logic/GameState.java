package org.murlan.live.game.logic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Rank;
import org.murlan.live.game.logic.handler.OnGameFinish;
import org.murlan.live.game.logic.handler.OnGameStart;
import org.murlan.live.game.logic.handler.OnTurnTimeout;
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
import java.util.stream.Collectors;

@Getter
@Setter
@Builder(setterPrefix = "with")
@AllArgsConstructor
public class GameState {
    private static final Logger log = LogManager.getLogger(GameState.class);

    private State state;
    private List<Player> players;
    private Map<Player, Short> score;

    @JsonIgnore private final long turnDurationInSeconds = GameConstants.TURN_DURATION_SECONDS;
    @JsonIgnore private Player currTurnPlayer;
    @JsonIgnore private boolean shouldCurrTurnPlayerUseThreeOfSpades;
    @JsonIgnore private CardCombination currCardCombination;

    @JsonIgnore private OnGameStart onGameStart;
    @JsonIgnore private OnGameFinish onGameFinish;
    @JsonIgnore private OnTurnTimeout onTurnTimeout;

    @JsonIgnore private Player prevWinner;
    @JsonIgnore private Player prevLoser;
    @JsonIgnore private Set<Player> givenCards;
    @JsonIgnore private PassCounter passCounter;
    @JsonIgnore private boolean isFirstMove;
    @JsonIgnore private List<Player> readyPlayers = new ArrayList<>();

    /* Turn timer */
    @JsonIgnore private ScheduledExecutorService scheduler;
    @JsonIgnore private ScheduledFuture<?> turnTimer;

    public GameState(State state, Player player, OnGameStart onGameStart, OnGameFinish onGameFinish, OnTurnTimeout onTurnTimeout) {
        this.state = state;
        this.players = new ArrayList<>();
        this.players.add(player);
        this.score = HashMap.newHashMap(GameConstants.MAX_PLAYERS);
        this.givenCards = HashSet.newHashSet(0);
        this.onGameStart = onGameStart;
        this.onGameFinish = onGameFinish;
        this.onTurnTimeout = onTurnTimeout;
    }

    public static GameState fromPrevious(GameState previous, Player winner, Player loser) {
        return GameState.builder()
                .withState(State.WAITING)
                .withPlayers(new ArrayList<>(previous.getPlayers()))
                .withScore(HashMap.newHashMap(GameConstants.MAX_PLAYERS))
                .withGivenCards(HashSet.newHashSet(0))
                .withOnGameStart(previous.getOnGameStart())
                .withOnGameFinish(previous.getOnGameFinish())
                .withOnTurnTimeout(previous.getOnTurnTimeout())
                .withPrevWinner(winner)
                .withPrevLoser(loser)
                .withReadyPlayers(previous.getReadyPlayers())
                .build();
    }

    @JsonIgnore
    public boolean isFromPrevious() {
        return prevLoser != null && prevWinner != null;
    }

    public boolean addPlayer(Player player, Runnable onSuccess) {
        if (players.size() == GameConstants.MAX_PLAYERS) {
            return false;
        }

        this.players.add(player);
        onSuccess.run();

        return true;
    }


    public boolean playHand(Player player, CardCombination cardCombination) {
        if (this.state != State.PLAYING) {
            log.info("this.state != State.PLAYING, {}", this.state.name());
            return false;
        }
        if (isNotPlayerTurn(player)) {
            log.info("isNotPlayerTurn {}", this.currTurnPlayer);
            return false;
        }
        if (!this.currTurnPlayer.getHand().contains(cardCombination)) {
            log.info("!this.currTurnPlayer.getHand().contains(cardCombination)");
            return false;
        }
        if (!MovePipeline.validate(cardCombination)) {
            log.info("Move is invalid");
            return false;
        }

        if (this.isFirstMove && this.shouldCurrTurnPlayerUseThreeOfSpades && !cardCombination.getCards().contains(Card.THREE_OF_SPADES)) {
            log.info("this.isFirstMove && shouldCurrTurnPlayerUseThreeOfSpades && !cardCombination.getCards().contains(Card.THREE_OF_SPADES)");
            return false;
        }

        if (!this.isFirstMove && (this.currCardCombination.isEqualStrength(cardCombination) || this.currCardCombination.isStrongerThan(cardCombination))) {
            log.info("!this.isFirstMove && (this.currCardCombination.isEqualStrength(cardCombination) || this.currCardCombination.isStrongerThan(cardCombination))");
            return false;
        }

        this.currTurnPlayer.getHand().removeCards(cardCombination);
        this.currCardCombination = cardCombination;

        this.passCounter.reset();
        if (this.currTurnPlayer.getHand().isEmpty()) {
            this.score.put(this.currTurnPlayer, (short) (GameConstants.MAX_PLAYERS - this.score.size() - 1));
            this.passCounter.resetAfterEmptyHand();
        }

        this.isFirstMove = false;

        nextTurn();

        return true;
    }

    public boolean pass(Player player) {
        return pass(player, false);
    }

    public boolean pass(Player player, boolean ignoreEmptyCardCombination) {
        if (this.state != State.PLAYING) {
            return false;
        }

        if (isNotPlayerTurn(player)) {
            return false;
        }

        if (!ignoreEmptyCardCombination && this.currCardCombination == GameConstants.EMPTY_CARD_COMBINATION) {
            // a player cannot pass when it is their turn AND they can play whatever they want
            return false;
        }

        this.passCounter.increment();
        if (this.passCounter.getCounter() == this.getPlayers().size() - this.score.size() - 1) {
            this.currCardCombination = GameConstants.EMPTY_CARD_COMBINATION;
            this.passCounter.reset();
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

        if (prevWinner.equals(player)) {
            if (card.hasBiggerRank(Rank.TEN)) {
                return false;
            }
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

    public boolean ready(Player player) {
        if (this.state != State.WAITING) {
            return false;
        }

        if (this.readyPlayers.contains(player)) {
            return false;
        }

        this.readyPlayers.add(player);

        return true;
    }

    public void handlePlayerNotInRoom(Player player, boolean hasLostConnection) {
        Optional<Player> optionalPlayer = this.players.stream().filter(player::equals).findAny();
        if (optionalPlayer.isEmpty()) {
            return;
        }

        if (this.state == State.PLAYING) {
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
        } else if (this.state == State.WAITING) {
            this.readyPlayers.remove(player);
        }
    }

    public synchronized void startGame() {
        onGameStart.accept(this);
    }

    private void finishGame() {
        if (State.FINISHED.equals(this.state)) {
            return;
        }

        cancelTurnTimer();

        this.state = State.FINISHED;
        onGameFinish.run();
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

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    public Map<Long, Short> getNumOfCardsPerPlayerId() {
        return players.stream()
                .collect(Collectors.toMap(Player::getId, p -> (short) p.getHand().size()));
    }

    @JsonIgnore
    public boolean shouldGameStart() {
        return this.state == State.WAITING && this.readyPlayers.size() == GameConstants.MAX_PLAYERS;
    }

    public enum State {
        WAITING,
        GIVING_CARDS,
        PLAYING,
        FINISHED
    }
}
