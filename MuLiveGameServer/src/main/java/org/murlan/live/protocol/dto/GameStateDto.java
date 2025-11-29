package org.murlan.live.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private String deck;
}
