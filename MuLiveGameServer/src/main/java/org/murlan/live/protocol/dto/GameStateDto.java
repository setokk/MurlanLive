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
    private PlayerMinimizedDto currTurnPlayer;
    private List<PlayerMinimizedDto> players;
    private String currCardCombination;
    private String hand;
    private Map<Long, Short> numOfCardsPerPlayerId;
    private PlayerMinimizedDto prevWinner;
    private PlayerMinimizedDto prevLoser;
    private long turnDurationInSeconds;
    private List<PlayerMinimizedDto> readyPlayers;

    public static GameStateDto from(Room room, Player player, ProtocolConfig config) {
        GameStateDtoBuilder builder = GameStateDto.builder()
                .state(room.getActiveGameState().getState().ordinal())
                .totalGamesPlayed(room.getTotalFinishedGames())
                .players(room.getActiveGameState().getPlayers().stream().map(PlayerMinimizedDto::fromPlayer).toList())
                .prevWinner(PlayerMinimizedDto.fromPlayer(room.getActiveGameState().getPrevWinner()))
                .prevLoser(PlayerMinimizedDto.fromPlayer(room.getActiveGameState().getPrevLoser()))
                .currTurnPlayer(PlayerMinimizedDto.fromPlayer(room.getActiveGameState().getCurrTurnPlayer()))
                .turnDurationInSeconds(room.getActiveGameState().getTurnDurationInSeconds())
                .readyPlayers(room.getActiveGameState().getReadyPlayers().stream().map(PlayerMinimizedDto::fromPlayer).toList());

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
                .players(gameState.getPlayers().stream().map(PlayerMinimizedDto::fromPlayer).toList())
                .prevWinner(PlayerMinimizedDto.fromPlayer(gameState.getPrevWinner()))
                .prevLoser(PlayerMinimizedDto.fromPlayer(gameState.getPrevLoser()))
                .currTurnPlayer(PlayerMinimizedDto.fromPlayer(gameState.getCurrTurnPlayer()))
                .turnDurationInSeconds(gameState.getTurnDurationInSeconds())
                .readyPlayers(room.getActiveGameState().getReadyPlayers().stream().map(PlayerMinimizedDto::fromPlayer).toList());

        if (GameState.State.PLAYING.equals(gameState.getState()) || GameState.State.GIVING_CARDS.equals(gameState.getState())) {
            builder.currCardCombination(gameState.getCurrCardCombination().toMessage(config.getProtocol_list_delimiter()))
                    .numOfCardsPerPlayerId(gameState.getNumOfCardsPerPlayerId());
        }

        return builder.build();
    }
}
