package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

@Getter
public class ForgotPasswordRequest implements IRequest {
    @NotNull(message = "[ForgotPasswordRequest]: usernameOrEmail cannot be null")
    @NotEmpty(message = "[ForgotPasswordRequest]: usernameOrEmail cannot be empty")
    private final String usernameOrEmail;

    @JsonCreator
    public ForgotPasswordRequest(@JsonProperty("usernameOrEmail") String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
}
