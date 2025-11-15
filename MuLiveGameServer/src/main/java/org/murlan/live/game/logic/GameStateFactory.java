package org.murlan.live.game.logic;

import lombok.RequiredArgsConstructor;
import org.murlan.live.game.GameConstants;
import org.murlan.live.game.deck.Card;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.deck.Deck;
import org.murlan.live.game.deck.Shuffler;
import org.murlan.live.protocol.dto.PlayerDto;
import org.murlan.live.protocol.rest.RoomRESTClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class GameStateFactory {
    private final RoomRESTClient roomRESTClient;

    public GameState createGameState(Room room) {
        Consumer<GameState> onStartGame = (gameState) -> {
            gameState.setState(GameState.State.PLAYING);
            gameState.setCurrCardCombination(GameConstants.EMPTY_CARD_COMBINATION);

            List<PlayerDto> players = gameState.getPlayers();
            List<Deck> decks = Shuffler.shuffle(players.size());
            for (int i = 0; i < players.size(); i++) {
                players.get(i).setDeck(decks.get(i));
            }

            if (room.getTotalPlayedGames() == 0) {
                gameState.setCurrTurnPlayer(gameState.findPlayerWithCardCombination(new CardCombination(Card.THREE_OF_SPADES)));
            } else {
                boolean loserContainsBothJokers = gameState.getPrevLoser().getDeck().contains(new CardCombination(Card.BLACK_JOKER, Card.RED_JOKER));
                gameState.setCurrTurnPlayer(loserContainsBothJokers ? gameState.getPrevWinner() : gameState.getPrevLoser());
            }
        };

        Runnable onFinishGame = () -> {
            Optional<PlayerDto> optionalFinalWinner = room.getTotalScores().entrySet().stream()
                    .filter(s -> s.getValue() >= room.getTotalScoreToWin())
                    .map(Map.Entry::getKey)
                    .findAny();

            if (optionalFinalWinner.isPresent()) {
                try {
                    roomRESTClient.saveRoom(room);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Map<PlayerDto, Short> previousScore = room.getActiveGameState().getScore();
                PlayerDto winner = previousScore.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .orElseThrow(() -> new IllegalStateException(""))
                        .getKey();
                PlayerDto loser = previousScore.entrySet().stream()
                        .min(Map.Entry.comparingByValue())
                        .orElseThrow(() -> new IllegalStateException(""))
                        .getKey();
                room.startNewGameFromPreviousGame(winner, loser);
            }
        };

        return new GameState(GameState.State.WAITING, room.getOwner(), onStartGame, onFinishGame);
    }
}
