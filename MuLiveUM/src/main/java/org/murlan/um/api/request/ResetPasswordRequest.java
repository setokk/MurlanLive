package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

@Getter
public class ResetPasswordRequest implements IRequest {
    @NotNull(message = "[ResetPasswordRequest]: token cannot be null")
    @NotEmpty(message = "[ResetPasswordRequest]: token cannot be empty")
    private final String token;

    @NotNull(message = "[ResetPasswordRequest]: newPassword cannot be null")
    @NotEmpty(message = "[ResetPasswordRequest]: newPassword cannot be empty")
    private final String newPassword;

    @JsonCreator
    public ResetPasswordRequest(
            @JsonProperty("token") String token,
            @JsonProperty("newPassword") String newPassword
    ) {
        this.token = token;
        this.newPassword = newPassword;
    }
}
