package org.murlan.um.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.murlan.um.api.request.validation.IRequest;

public class CreateRoomRequest implements IRequest {
    @NotNull(message = "[CreateRoomRequest]: id cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: id cannot be empty")
    private String id;

    @NotNull(message = "[CreateRoomRequest]: name cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: name cannot be empty")
    private String name;
}
