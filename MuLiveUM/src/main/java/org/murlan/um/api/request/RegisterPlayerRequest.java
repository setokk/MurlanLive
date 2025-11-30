package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.murlan.um.api.request.validation.IRequest;

@Getter
public class RegisterPlayerRequest implements IRequest {
    @NotNull(message = "[RegisterPlayerRequest]: username field is mandatory")
    @NotEmpty(message = "[RegisterPlayerRequest]: username field cannot be empty")
    private final String username;

    @NotNull(message = "[RegisterPlayerRequest]: password field is mandatory")
    @NotEmpty(message = "[RegisterPlayerRequest]: password field cannot be empty")
    private final String password;

    @JsonCreator
    public RegisterPlayerRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }
}
