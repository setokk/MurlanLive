package org.murlan.um.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.murlan.um.api.request.CheckHandLayoutConfigurationRequest;
import org.murlan.um.api.request.UpdateHandLayoutConfigurationRequest;
import org.murlan.um.model.dto.HandLayoutDto;
import org.murlan.um.service.HandLayoutConfigurationService;
import org.murlan.um.service.mapper.HandLayoutConfigurationMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/hand-layout-configuration")
@RequiredArgsConstructor
public class HandLayoutConfigurationController {
    private final HandLayoutConfigurationService handLayoutConfigurationService;
    private final HandLayoutConfigurationMapper handLayoutConfigurationMapper;

    @PostMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody UpdateHandLayoutConfigurationRequest request) throws NoSuchAlgorithmException, JsonProcessingException {
        handLayoutConfigurationService.update(handLayoutConfigurationMapper.toParam(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check")
    public ResponseEntity<List<HandLayoutDto>> check(@Valid @RequestBody CheckHandLayoutConfigurationRequest request) {
        Optional<List<HandLayoutDto>> handLayouts = handLayoutConfigurationService.check(request.getSha());
        return handLayouts.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
