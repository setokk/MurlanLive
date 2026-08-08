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

    public static GameStateDto from(Room room, Player player, ProtocolConfig config) {
        return GameStateDto.builder()
                .state(room.getActiveGameState().getState().ordinal())
                .totalGamesPlayed(room.getTotalFinishedGames())
                .currTurnPlayer(room.getActiveGameState().getCurrTurnPlayer())
                .players(room.getActiveGameState().getPlayers())
                .currCardCombination(room.getActiveGameState().getCurrCardCombination().toMessage(config.getProtocol_list_delimiter()))
                .hand(new CardCombination(player.getHand()).toMessage(config.getProtocol_list_delimiter()))
                .numOfCardsPerPlayerId(room.getActiveGameState().getNumOfCardsPerPlayerId())
                .prevWinner(room.getActiveGameState().getPrevWinner())
                .prevLoser(room.getActiveGameState().getPrevLoser())
                .build();
    }

    public static GameStateDto from(GameState gameState, Room room, ProtocolConfig config) {
        return GameStateDto.builder()
                .state(gameState.getState().ordinal())
                .totalGamesPlayed(room.getTotalFinishedGames())
                .currTurnPlayer(gameState.getCurrTurnPlayer())
                .players(gameState.getPlayers())
                .currCardCombination(gameState.getCurrCardCombination().toMessage(config.getProtocol_list_delimiter()))
                .numOfCardsPerPlayerId(gameState.getNumOfCardsPerPlayerId())
                .prevWinner(gameState.getPrevWinner())
                .prevLoser(gameState.getPrevLoser())
                .build();
    }
}
