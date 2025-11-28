package org.murlan.um.api.request.gameserver;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.murlan.um.api.dto.PlayerDto;

import java.util.List;
import java.util.Map;

public final class GameState {
    @NotNull(message = "[CreateRoomRequest]: state cannot be null")
    private Short state;

    @NotNull(message = "[CreateRoomRequest]: players cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: players cannot be empty")
    private List<PlayerDto> players;

    @NotNull(message = "[CreateRoomRequest]: score cannot be null")
    private Map<PlayerDto, Short> score;
}
