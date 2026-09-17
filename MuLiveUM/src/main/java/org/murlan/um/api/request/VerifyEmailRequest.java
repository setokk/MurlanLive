package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

@Getter
public class VerifyEmailRequest implements IRequest {
    @NotNull(message = "[ResetPasswordRequest]: token cannot be null")
    @NotEmpty(message = "[ResetPasswordRequest]: token cannot be empty")
    private final String token;

    @JsonCreator
    public VerifyEmailRequest(@JsonProperty("token") String token) {
        this.token = token;
    }
}
