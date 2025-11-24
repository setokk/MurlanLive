package org.murlan.um.api.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.um.api.request.validation.IRequest;

@Getter
@AllArgsConstructor
public class LoginPlayerRequest implements IRequest {
    @NotNull(message = "[LoginPlayerRequest]: username field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: username field cannot be empty")
    private String username;

    @NotNull(message = "[LoginPlayerRequest]: password field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: password field cannot be empty")
    private String password;
}
