package org.murlan.um.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.murlan.um.model.HandLayoutConfigurationEntity;
import org.murlan.um.model.dto.HandLayoutDto;
import org.murlan.um.model.dto.PlayerDto;
import org.murlan.um.repository.HandLayoutConfigurationRepository;
import org.murlan.um.service.mapper.HandLayoutConfigurationMapper;
import org.murlan.um.service.param.UpdateHandLayoutConfigurationParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HandLayoutConfigurationService {
    private final HandLayoutConfigurationRepository handLayoutConfigurationRepository;
    private final HandLayoutConfigurationMapper handLayoutConfigurationMapper;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Transactional
    public void update(UpdateHandLayoutConfigurationParam param) throws NoSuchAlgorithmException, JsonProcessingException {
        byte[] jsonBytes = objectMapper.writeValueAsBytes(param.handLayouts());
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String sha = HexFormat.of().formatHex(digest.digest(jsonBytes));

        PlayerDto player = authService.getAuthenticatedUser();

        HandLayoutConfigurationEntity entityToBeSaved = handLayoutConfigurationMapper.toEntity(
                param.handLayouts(),
                sha,
                player.getId()
        );

        handLayoutConfigurationRepository.findByPlayerId(player.getId())
                .ifPresent(entity -> entityToBeSaved.setId(entity.getId()));

        handLayoutConfigurationRepository.save(entityToBeSaved);
    }

    public Optional<List<HandLayoutDto>> check(String sha) {
        PlayerDto player = authService.getAuthenticatedUser();

        Optional<HandLayoutConfigurationEntity> handLayoutConfiguration = handLayoutConfigurationRepository.findByPlayerId(player.getId());
        if (handLayoutConfiguration.isPresent() && !sha.equals(handLayoutConfiguration.get().getSha())) {
            return Optional.of(handLayoutConfigurationMapper.toDto(handLayoutConfiguration.get()));
        } else {
            return Optional.empty();
        }
    }
}
