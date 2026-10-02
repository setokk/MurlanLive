package org.murlan.um.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.murlan.um.api.request.UpdateHandLayoutConfigurationRequest;
import org.murlan.um.model.dto.HandLayoutConfigurationDto;
import org.murlan.um.service.HandLayoutConfigurationService;
import org.murlan.um.service.mapper.HandLayoutConfigurationMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hand-layout-configuration")
@RequiredArgsConstructor
public class HandLayoutConfigurationController {
    private final HandLayoutConfigurationService handLayoutConfigurationService;
    private final HandLayoutConfigurationMapper handLayoutConfigurationMapper;

    @PostMapping("/update")
    public ResponseEntity<HandLayoutConfigurationDto> update(@Valid @RequestBody UpdateHandLayoutConfigurationRequest request) {
        HandLayoutConfigurationDto handLayoutConfigurationDto = handLayoutConfigurationService.update(
                handLayoutConfigurationMapper.toParam(request)
        );
        return ResponseEntity.ok(handLayoutConfigurationDto);
    }
}
