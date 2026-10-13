package org.murlan.um.service.param;

import org.murlan.um.model.dto.HandLayoutDto;

import java.util.List;

public record UpdateHandLayoutConfigurationParam(List<HandLayoutDto> handLayouts) {
}
