package org.murlan.um.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.um.validation.IValidatable;

@Getter
@AllArgsConstructor
public class LoginPlayerRequest implements IValidatable {
    @NotNull(message = "[LoginPlayerRequest]: username field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: username field cannot be empty")
    private String username;

    @NotNull(message = "[LoginPlayerRequest]: password field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: password field cannot be empty")
    private String password;
}
