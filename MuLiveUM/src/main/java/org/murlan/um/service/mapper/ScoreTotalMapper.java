package org.murlan.um.service.mapper;

import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.model.ScoreTotalEntity;
import org.murlan.um.model.pk.ScoreTotalPK;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class ScoreTotalMapper {
    public ScoreTotalEntity toEntity(Map.Entry<PlayerDto, Short> dto, String roomId, boolean isWinner) {
        return ScoreTotalEntity.builder()
                .id(new ScoreTotalPK(dto.getKey().getId(), roomId))
                .isWinner(isWinner)
                .score(dto.getValue())
                .build();
    }
}
