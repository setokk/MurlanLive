package org.murlan.live.protocol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GameFinishDto {
    private Map<Long, Short> scorePerPlayerId;
    private Long winnerPlayerId;
    private Long loserPlayerId;
}
