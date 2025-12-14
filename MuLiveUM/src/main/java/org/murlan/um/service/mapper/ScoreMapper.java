package org.murlan.um.service.mapper;

import jakarta.validation.constraints.NotNull;
import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.model.GameStateEntity;
import org.murlan.um.model.ScoreEntity;
import org.murlan.um.model.pk.ScorePK;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public final class ScoreMapper {
    public ScoreEntity toEntity(Map.Entry<String, Short> dto, GameStateEntity gameStateEntity) {
        return ScoreEntity.builder()
                .id(new ScorePK(Long.valueOf(dto.getKey()), null))
                .score(dto.getValue())
                .gameState(gameStateEntity)
                .build();
    }

    public Map<String, Short> toDto(List<ScoreEntity> entities) {
        return entities.stream()
                .collect(Collectors.toMap(
                        (score) -> String.valueOf(score.getId().getPlayerId()),
                        ScoreEntity::getScore
                ));
    }
}
