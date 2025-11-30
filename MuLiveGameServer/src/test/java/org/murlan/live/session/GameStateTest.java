package org.murlan.live.session;

import junit.framework.TestCase;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.protocol.dto.Player;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GameStateTest extends TestCase {
    public void testPlayHand_SameRankSingle() {
        Player currPlayer = new Player(1L, "currPlayer", LocalDateTime.now(), "currPlayer");
        Player otherPlayer = new Player(2L, "otherPlayer", LocalDateTime.now(), "otherPlayer");
        currPlayer.setDeck(new Deck(getListOfCards(Card.FIVE_OF_CLUBS, Card.FIVE_OF_DIAMONDS)));
        otherPlayer.setDeck(new Deck(getListOfCards(Card.FIVE_OF_SPADES, Card.FIVE_OF_HEARTS)));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES);
        initialCardCombination.setType(CardCombinationType.SINGLE_CARD);
        GameState gameState = GameState.builder()
                .withCurrTurnPlayer(currPlayer)
                .withState(GameState.State.PLAYING)
                .withPlayers(List.of(currPlayer, otherPlayer))
                .withScore(HashMap.newHashMap(4))
                .withCurrCardCombination(initialCardCombination)
                .build();

        boolean isFirstMoveSuccessful = gameState.playHand(currPlayer, new CardCombination(Card.FIVE_OF_CLUBS));
        assertTrue(isFirstMoveSuccessful);

        boolean isSecondMoveSuccessful = gameState.playHand(otherPlayer, new CardCombination(Card.FIVE_OF_SPADES));
        assertFalse(isSecondMoveSuccessful);
    }

    public void testPlayHand_SameRankDouble() {
        Player currPlayer = new Player(1L, "currPlayer", LocalDateTime.now(), "currPlayer");
        Player otherPlayer = new Player(2L, "otherPlayer", LocalDateTime.now(), "otherPlayer");
        currPlayer.setDeck(new Deck(getListOfCards(Card.TWO_OF_CLUBS, Card.TWO_OF_DIAMONDS, Card.TEN_OF_DIAMONDS)));
        otherPlayer.setDeck(new Deck(getListOfCards(Card.TWO_OF_SPADES, Card.TWO_OF_HEARTS, Card.TEN_OF_HEARTS)));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES, Card.THREE_OF_CLUBS);
        initialCardCombination.setType(CardCombinationType.DOUBLE_CARDS);
        GameState gameState = GameState.builder()
                .withCurrTurnPlayer(currPlayer)
                .withState(GameState.State.PLAYING)
                .withPlayers(List.of(currPlayer, otherPlayer))
                .withScore(HashMap.newHashMap(4))
                .withCurrCardCombination(initialCardCombination)
                .build();

        List<Card> currCardsToPlay = getListOfCards(Card.TWO_OF_CLUBS, Card.TWO_OF_DIAMONDS);
        List<Card> otherCardsToPlay = getListOfCards(Card.TWO_OF_SPADES, Card.TWO_OF_HEARTS);

        boolean isFirstMoveSuccessful = gameState.playHand(currPlayer, new CardCombination(currCardsToPlay));
        assertTrue(isFirstMoveSuccessful);

        boolean isSecondMoveSuccessful = gameState.playHand(otherPlayer, new CardCombination(otherCardsToPlay));
        assertFalse(isSecondMoveSuccessful);
    }

    public void testPlayHand_TripleWithDoubleCard() {
        Player currPlayer = new Player(1L, "currPlayer", LocalDateTime.now(), "currPlayer");
        Player otherPlayer = new Player(2L, "otherPlayer", LocalDateTime.now(), "otherPlayer");
        currPlayer.setDeck(new Deck(getListOfCards(Card.FIVE_OF_SPADES, Card.FIVE_OF_CLUBS, Card.FIVE_OF_HEARTS, Card.ACE_OF_CLUBS)));
        otherPlayer.setDeck(new Deck(getListOfCards(Card.TWO_OF_SPADES, Card.TWO_OF_HEARTS, Card.JACK_OF_CLUBS)));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES, Card.THREE_OF_CLUBS, Card.THREE_OF_HEARTS);
        initialCardCombination.setType(CardCombinationType.TRIPLE_CARDS);
        GameState gameState = GameState.builder()
                .withCurrTurnPlayer(currPlayer)
                .withState(GameState.State.PLAYING)
                .withPlayers(List.of(currPlayer, otherPlayer))
                .withScore(HashMap.newHashMap(4))
                .withCurrCardCombination(initialCardCombination)
                .build();

        List<Card> currCardsToPlay = getListOfCards(Card.FIVE_OF_SPADES, Card.FIVE_OF_CLUBS, Card.FIVE_OF_HEARTS);
        List<Card> otherCardsToPlay = getListOfCards(Card.TWO_OF_SPADES, Card.TWO_OF_HEARTS);

        boolean isFirstMoveSuccessful = gameState.playHand(currPlayer, new CardCombination(currCardsToPlay));
        assertTrue(isFirstMoveSuccessful);

        boolean isSecondMoveSuccessful = gameState.playHand(otherPlayer, new CardCombination(otherCardsToPlay));
        assertFalse(isSecondMoveSuccessful);
    }

    private List<Card> getListOfCards(Card... card) {
        return Arrays.stream(card).collect(Collectors.toList());
    }
}