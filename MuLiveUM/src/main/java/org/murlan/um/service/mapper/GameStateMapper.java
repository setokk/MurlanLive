package org.murlan.um.service.mapper;

import org.murlan.um.api.dto.GameStateDto;
import org.murlan.um.core.logic.GameStateEnum;
import org.murlan.um.model.GameStateEntity;
import org.murlan.um.model.ScoreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class GameStateMapper {
    private final ScoreMapper scoreMapper;

    @Autowired
    public GameStateMapper(ScoreMapper scoreMapper) {
        this.scoreMapper = scoreMapper;
    }

    public GameStateEntity toEntity(GameStateDto dto) {
        GameStateEntity entity = GameStateEntity.builder()
                .state(GameStateEnum.fromOrdinal(dto.getState()))
                .build();

        List<ScoreEntity> scores = dto.getScore().entrySet().stream()
                .map(scoreDto -> scoreMapper.toEntity(scoreDto, entity))
                .toList();
        entity.setScores(scores);

        return entity;
    }
}
