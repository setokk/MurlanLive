package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

@Getter
public class LoginPlayerRequest implements IRequest {
    @NotNull(message = "[LoginPlayerRequest]: usernameOrEmail field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: usernameOrEmail field cannot be empty")
    private final String usernameOrEmail;

    @NotNull(message = "[LoginPlayerRequest]: password field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: password field cannot be empty")
    private final String password;

    @JsonCreator
    public LoginPlayerRequest(@JsonProperty("usernameOrEmail") String usernameOrEmail, @JsonProperty("password") String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
}
