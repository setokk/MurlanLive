package org.murlan.um.service.mapper;

import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.model.RoomEntity;
import org.murlan.um.model.ScoreTotalEntity;
import org.murlan.um.model.pk.ScoreTotalPK;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public final class ScoreTotalMapper {
    public ScoreTotalEntity toEntity(Map.Entry<PlayerDto, Short> dto, RoomEntity room, boolean isWinner) {
        return ScoreTotalEntity.builder()
                .id(new ScoreTotalPK(dto.getKey().getId(), room.getId()))
                .room(room)
                .isWinner(isWinner)
                .score(dto.getValue())
                .build();
    }

    public Map<String, Short> toDtos(List<ScoreTotalEntity> entities) {
        return entities.stream()
                .collect(Collectors.toMap(
                        (scoreTotal) -> String.valueOf(scoreTotal.getId().getPlayerId()),
                        ScoreTotalEntity::getScore
                ));
    }
}
