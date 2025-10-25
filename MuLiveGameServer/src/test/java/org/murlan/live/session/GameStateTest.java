package org.murlan.live.session;

import junit.framework.TestCase;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.protocol.dto.PlayerDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class GameStateTest extends TestCase {
    public void testPlayHand_SameRankSingle() {
        PlayerDto currPlayer = new PlayerDto(1L, "currPlayer", "ignored", "currPlayer");
        PlayerDto otherPlayer = new PlayerDto(2L, "otherPlayer", "ignored", "otherPlayer");
        currPlayer.setDeck(new Deck(getListOfCards(Card.FIVE_OF_CLUBS, Card.FIVE_OF_DIAMONDS)));
        otherPlayer.setDeck(new Deck(getListOfCards(Card.FIVE_OF_SPADES, Card.FIVE_OF_HEARTS)));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES);
        initialCardCombination.setType(CardCombinationType.SINGLE_CARD);
        GameState gameState = new GameState(
                currPlayer,
                GameState.State.PLAYING,
                List.of(currPlayer, otherPlayer),
                new HashMap<>(),
                initialCardCombination
        );

        boolean isFirstMoveSuccessful = gameState.playHand(currPlayer, new CardCombination(Card.FIVE_OF_CLUBS));
        assertTrue(isFirstMoveSuccessful);

        boolean isSecondMoveSuccessful = gameState.playHand(otherPlayer, new CardCombination(Card.FIVE_OF_SPADES));
        assertFalse(isSecondMoveSuccessful);
    }

    public void testPlayHand_SameRankDouble() {
        PlayerDto currPlayer = new PlayerDto(1L, "currPlayer", "ignored", "currPlayer");
        PlayerDto otherPlayer = new PlayerDto(2L, "otherPlayer", "ignored", "otherPlayer");
        currPlayer.setDeck(new Deck(getListOfCards(Card.TWO_OF_CLUBS, Card.TWO_OF_DIAMONDS, Card.TEN_OF_DIAMONDS)));
        otherPlayer.setDeck(new Deck(getListOfCards(Card.TWO_OF_SPADES, Card.TWO_OF_HEARTS, Card.TEN_OF_HEARTS)));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES, Card.THREE_OF_CLUBS);
        initialCardCombination.setType(CardCombinationType.DOUBLE_CARDS);
        GameState gameState = new GameState(
                currPlayer,
                GameState.State.PLAYING,
                List.of(currPlayer, otherPlayer),
                new HashMap<>(),
                initialCardCombination
        );

        List<Card> currCardsToPlay = getListOfCards(Card.TWO_OF_CLUBS, Card.TWO_OF_DIAMONDS);
        List<Card> otherCardsToPlay = getListOfCards(Card.TWO_OF_SPADES, Card.TWO_OF_HEARTS);

        boolean isFirstMoveSuccessful = gameState.playHand(currPlayer, new CardCombination(currCardsToPlay));
        assertTrue(isFirstMoveSuccessful);

        boolean isSecondMoveSuccessful = gameState.playHand(otherPlayer, new CardCombination(otherCardsToPlay));
        assertFalse(isSecondMoveSuccessful);
    }

    public void testPlayHand_TripleWithDoubleCard() {
        PlayerDto currPlayer = new PlayerDto(1L, "currPlayer", "ignored", "currPlayer");
        PlayerDto otherPlayer = new PlayerDto(2L, "otherPlayer", "ignored", "otherPlayer");
        currPlayer.setDeck(new Deck(getListOfCards(Card.FIVE_OF_SPADES, Card.FIVE_OF_CLUBS, Card.FIVE_OF_HEARTS, Card.ACE_OF_CLUBS)));
        otherPlayer.setDeck(new Deck(getListOfCards(Card.TWO_OF_SPADES, Card.TWO_OF_HEARTS, Card.JACK_OF_CLUBS)));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES, Card.THREE_OF_CLUBS, Card.THREE_OF_HEARTS);
        initialCardCombination.setType(CardCombinationType.TRIPLE_CARDS);
        GameState gameState = new GameState(
                currPlayer,
                GameState.State.PLAYING,
                List.of(currPlayer, otherPlayer),
                new HashMap<>(),
                initialCardCombination
        );

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