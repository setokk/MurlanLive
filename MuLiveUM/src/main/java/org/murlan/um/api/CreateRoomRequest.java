package org.murlan.um.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.murlan.um.api.validation.IRequest;

public class CreateRoomRequest implements IRequest {
    @NotNull(message = "[CreateRoomRequest]: roomId cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: roomId cannot be empty")
    private String roomId;

    @NotNull(message = "[CreateRoomRequest]: roomId cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: roomId cannot be empty")
    private String roomId;

    @NotNull(message = "[CreateRoomRequest]: roomId cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: roomId cannot be empty")
    private String roomId;
}
