package org.murlan.um.service.mapper;

import org.murlan.um.api.request.UpdateHandLayoutConfigurationRequest;
import org.murlan.um.model.HandLayoutConfigurationEntity;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.dto.HandLayoutConfigurationDto;
import org.murlan.um.service.param.UpdateHandLayoutConfigurationParam;
import org.springframework.stereotype.Component;

@Component
public class HandLayoutConfigurationMapper {
    public UpdateHandLayoutConfigurationParam toParam(UpdateHandLayoutConfigurationRequest request) {
        return new UpdateHandLayoutConfigurationParam(request.getHandLayoutConfiguration());
    }

    public HandLayoutConfigurationDto toDto(HandLayoutConfigurationEntity entity) {
        return new HandLayoutConfigurationDto(entity.getHandLayouts());
    }

    public HandLayoutConfigurationEntity toEntity(HandLayoutConfigurationDto dto, Long playerId) {
        return new HandLayoutConfigurationEntity(
                null,
                dto.getHandLayouts(),
                PlayerEntity.builder().id(playerId).build()
        );
    }
}
