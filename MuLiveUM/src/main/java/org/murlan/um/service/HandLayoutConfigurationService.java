package org.murlan.um.service;

import lombok.RequiredArgsConstructor;
import org.murlan.um.model.HandLayoutConfigurationEntity;
import org.murlan.um.model.dto.HandLayoutConfigurationDto;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.repository.HandLayoutConfigurationRepository;
import org.murlan.um.service.mapper.HandLayoutConfigurationMapper;
import org.murlan.um.service.param.UpdateHandLayoutConfigurationParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HandLayoutConfigurationService {
    private final HandLayoutConfigurationRepository handLayoutConfigurationRepository;
    private final HandLayoutConfigurationMapper handLayoutConfigurationMapper;
    private final AuthService authService;

    @Transactional
    public HandLayoutConfigurationDto update(UpdateHandLayoutConfigurationParam param) {
        PlayerDto player = authService.getAuthenticatedUser();

        HandLayoutConfigurationEntity entityToBeSaved = handLayoutConfigurationMapper.toEntity(
                param.handLayoutConfiguration(),
                player.getId()
        );

        handLayoutConfigurationRepository.findByPlayerId(player.getId())
                .ifPresent(entity -> entityToBeSaved.setId(entity.getId()));

        return handLayoutConfigurationMapper.toDto(
                handLayoutConfigurationRepository.save(entityToBeSaved)
        );
    }
}
