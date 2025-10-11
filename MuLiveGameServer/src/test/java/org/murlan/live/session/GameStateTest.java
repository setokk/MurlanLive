package org.murlan.live.session;

import junit.framework.TestCase;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.CardCombinationType;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.protocol.dto.PlayerDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStateTest extends TestCase {
    public void testPlayHand_SameRankSingle() {
        PlayerDto currPlayer = new PlayerDto(1L, "currPlayer", "ignored", "currPlayer");
        PlayerDto otherPlayer = new PlayerDto(2L, "otherPlayer", "ignored", "otherPlayer");

        List<Card> currPlayerCards = new ArrayList<>(2);
        currPlayerCards.add(Card.FIVE_OF_CLUBS);
        currPlayerCards.add(Card.FIVE_OF_DIAMONDS);
        List<Card> otherPlayerCards = new ArrayList<>(2);
        otherPlayerCards.add(Card.FIVE_OF_SPADES);
        otherPlayerCards.add(Card.FIVE_OF_HEARTS);
        currPlayer.setDeck(new Deck(currPlayerCards));
        otherPlayer.setDeck(new Deck(otherPlayerCards));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES);
        initialCardCombination.setType(CardCombinationType.SINGLE_CARD);
        GameState gameState = new GameState(
                currPlayer,
                GameState.State.PLAYING,
                List.of(currPlayer, otherPlayer),
                Map.of(currPlayer, 0, otherPlayer, 0),
                initialCardCombination
        );

        Card cardToPlay = currPlayer.equals(gameState.getCurrTurnPlayer()) ? Card.FIVE_OF_CLUBS : Card.FIVE_OF_HEARTS;
        boolean isFirstMoveSuccessful = gameState.playHand(gameState.getCurrTurnPlayer(), new CardCombination(cardToPlay));
        assertTrue(isFirstMoveSuccessful);

        Card secondCardToPlay = currPlayer.equals(gameState.getCurrTurnPlayer()) ? Card.FIVE_OF_DIAMONDS : Card.FIVE_OF_SPADES;
        boolean isSecondMoveSuccessful = gameState.playHand(gameState.getCurrTurnPlayer(), new CardCombination(secondCardToPlay));
        assertFalse(isSecondMoveSuccessful);
    }

    public void testPlayHand_SameRankDouble() {
        PlayerDto currPlayer = new PlayerDto(1L, "currPlayer", "ignored", "currPlayer");
        PlayerDto otherPlayer = new PlayerDto(2L, "otherPlayer", "ignored", "otherPlayer");

        List<Card> currPlayerCards = new ArrayList<>(3);
        currPlayerCards.add(Card.TWO_OF_CLUBS);
        currPlayerCards.add(Card.TWO_OF_DIAMONDS);
        currPlayerCards.add(Card.TEN_OF_DIAMONDS);
        List<Card> otherPlayerCards = new ArrayList<>(3);
        otherPlayerCards.add(Card.TWO_OF_SPADES);
        otherPlayerCards.add(Card.TWO_OF_HEARTS);
        otherPlayerCards.add(Card.TEN_OF_HEARTS);
        currPlayer.setDeck(new Deck(currPlayerCards));
        otherPlayer.setDeck(new Deck(otherPlayerCards));

        CardCombination initialCardCombination = new CardCombination(Card.THREE_OF_SPADES, Card.THREE_OF_CLUBS);
        initialCardCombination.setType(CardCombinationType.DOUBLE_CARDS);
        GameState gameState = new GameState(
                currPlayer,
                GameState.State.PLAYING,
                List.of(currPlayer, otherPlayer),
                Map.of(currPlayer, 0, otherPlayer, 0),
                initialCardCombination
        );

        List<Card> currCardsToPlay = new ArrayList<>(2);
        currCardsToPlay.add(Card.TWO_OF_CLUBS);
        currCardsToPlay.add(Card.TWO_OF_DIAMONDS);
        List<Card> otherCardsToPlay = new ArrayList<>(2);
        otherCardsToPlay.add(Card.TWO_OF_SPADES);
        otherCardsToPlay.add(Card.TWO_OF_HEARTS);

        List<Card> cardsToPlay = currPlayer.equals(gameState.getCurrTurnPlayer())
                ? currCardsToPlay
                : otherCardsToPlay;
        boolean isFirstMoveSuccessful = gameState.playHand(gameState.getCurrTurnPlayer(), new CardCombination(cardsToPlay));
        assertTrue(isFirstMoveSuccessful);

        List<Card> secondCardsToPlay = currPlayer.equals(gameState.getCurrTurnPlayer())
                ? currCardsToPlay
                : otherCardsToPlay;
        boolean isSecondMoveSuccessful = gameState.playHand(gameState.getCurrTurnPlayer(), new CardCombination(secondCardsToPlay));
        assertFalse(isSecondMoveSuccessful);
    }
}