package org.murlan.um.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class GameStateDto {
    @NotNull(message = "[CreateRoomRequest]: state cannot be null")
    private Integer state;

    @NotNull(message = "[CreateRoomRequest]: players cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: players cannot be empty")
    private List<PlayerDto> players;

    @NotNull(message = "[CreateRoomRequest]: score cannot be null")
    private Map<String, Short> score;
}
