package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.um.api.request.validation.IRequest;

@Getter
public class LoginPlayerRequest implements IRequest {
    @NotNull(message = "[LoginPlayerRequest]: username field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: username field cannot be empty")
    private final String username;

    @NotNull(message = "[LoginPlayerRequest]: password field is mandatory")
    @NotEmpty(message = "[LoginPlayerRequest]: password field cannot be empty")
    private final String password;

    @JsonCreator
    public LoginPlayerRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }
}
