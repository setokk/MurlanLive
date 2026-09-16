package org.murlan.live.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.murlan.live.game.deck.CardCombination;
import org.murlan.live.game.logic.GameState;
import org.murlan.live.game.logic.Room;
import org.murlan.live.protocol.config.ProtocolConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GameStateDto {
    private int state;
    private int totalGamesPlayed;
    private Player currTurnPlayer;
    private List<Player> players;
    private String currCardCombination;
    private String hand;
    private Map<Long, Short> numOfCardsPerPlayerId;
    private Player prevWinner;
    private Player prevLoser;
    private long turnDurationInSeconds;
    private List<Player> readyPlayers;

    public static GameStateDto from(Room room, Player player, ProtocolConfig config) {
        GameStateDtoBuilder builder = GameStateDto.builder()
                .state(room.getActiveGameState().getState().ordinal())
                .totalGamesPlayed(room.getTotalFinishedGames())
                .players(room.getActiveGameState().getPlayers())
                .prevWinner(room.getActiveGameState().getPrevWinner())
                .prevLoser(room.getActiveGameState().getPrevLoser())
                .currTurnPlayer(room.getActiveGameState().getCurrTurnPlayer())
                .turnDurationInSeconds(room.getActiveGameState().getTurnDurationInSeconds())
                .readyPlayers(room.getActiveGameState().getReadyPlayers());

        GameState.State state = room.getActiveGameState().getState();
        if (GameState.State.PLAYING.equals(state) || GameState.State.GIVING_CARDS.equals(state)) {
            builder.currCardCombination(room.getActiveGameState().getCurrCardCombination().toMessage(config.getProtocol_list_delimiter()))
                    .hand(new CardCombination(player.getHand()).toMessage(config.getProtocol_list_delimiter()))
                    .numOfCardsPerPlayerId(room.getActiveGameState().getNumOfCardsPerPlayerId());
        }

        return builder.build();
    }

    public static GameStateDto from(GameState gameState, Room room, ProtocolConfig config) {
        GameStateDtoBuilder builder = GameStateDto.builder()
                .state(gameState.getState().ordinal())
                .totalGamesPlayed(room.getTotalFinishedGames())
                .players(gameState.getPlayers())
                .prevWinner(gameState.getPrevWinner())
                .prevLoser(gameState.getPrevLoser())
                .currTurnPlayer(gameState.getCurrTurnPlayer())
                .turnDurationInSeconds(gameState.getTurnDurationInSeconds())
                .readyPlayers(room.getActiveGameState().getReadyPlayers());

        if (GameState.State.PLAYING.equals(gameState.getState()) || GameState.State.GIVING_CARDS.equals(gameState.getState())) {
            builder.currCardCombination(gameState.getCurrCardCombination().toMessage(config.getProtocol_list_delimiter()))
                    .numOfCardsPerPlayerId(gameState.getNumOfCardsPerPlayerId());
        }

        return builder.build();
    }
}
