package org.murlan.um.service.mapper;

import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.model.GameStateEntity;
import org.murlan.um.model.ScoreEntity;
import org.murlan.um.model.pk.ScorePK;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class ScoreMapper {
    public ScoreEntity toEntity(Map.Entry<String, Short> dto, GameStateEntity gameStateEntity) {
        return ScoreEntity.builder()
                .id(new ScorePK(Long.valueOf(dto.getKey()), null))
                .score(dto.getValue())
                .gameState(gameStateEntity)
                .build();
    }
}
