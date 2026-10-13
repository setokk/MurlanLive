package org.murlan.um.service.mapper;

import org.murlan.um.api.request.UpdateHandLayoutConfigurationRequest;
import org.murlan.um.model.HandLayoutConfigurationEntity;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.dto.HandLayoutDto;
import org.murlan.um.service.param.UpdateHandLayoutConfigurationParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HandLayoutConfigurationMapper {
    public UpdateHandLayoutConfigurationParam toParam(UpdateHandLayoutConfigurationRequest request) {
        return new UpdateHandLayoutConfigurationParam(request.getHandLayouts());
    }

    public List<HandLayoutDto> toDto(HandLayoutConfigurationEntity entity) {
        return entity.getHandLayouts();
    }

    public HandLayoutConfigurationEntity toEntity(List<HandLayoutDto> dto, String sha, Long playerId) {
        return new HandLayoutConfigurationEntity(null, dto, sha, PlayerEntity.builder().id(playerId).build());
    }
}
