package org.murlan.um.api;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.um.api.validation.IValidatable;

@Getter
@AllArgsConstructor
public class RegisterPlayerRequest implements IValidatable {
    @NotNull(message = "[RegisterPlayerRequest]: username field is mandatory")
    @NotEmpty(message = "[RegisterPlayerRequest]: username field cannot be empty")
    private String username;

    @NotNull(message = "[RegisterPlayerRequest]: password field is mandatory")
    @NotEmpty(message = "[RegisterPlayerRequest]: password field cannot be empty")
    private String password;
}
