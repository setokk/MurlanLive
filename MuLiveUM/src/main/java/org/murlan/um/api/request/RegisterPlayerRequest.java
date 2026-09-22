package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;
import org.murlan.um.error.BusinessLogicException;
import org.springframework.http.HttpStatus;

import java.util.regex.Pattern;

@Getter
public class RegisterPlayerRequest implements IRequest {
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MAX_PASSWORD_LENGTH = 100;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9_.-]+$");

    @NotNull(message = "[RegisterPlayerRequest]: username field is mandatory")
    @NotEmpty(message = "[RegisterPlayerRequest]: username field cannot be empty")
    private final String username;

    @NotNull(message = "[RegisterPlayerRequest]: password field is mandatory")
    @NotEmpty(message = "[RegisterPlayerRequest]: password field cannot be empty")
    private final String password;

    private String email;

    @JsonCreator
    public RegisterPlayerRequest(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void preValidate() throws BusinessLogicException {
        BusinessLogicException e = new BusinessLogicException(HttpStatus.BAD_REQUEST);

        if (username.length() > MAX_USERNAME_LENGTH) {
            e.addErrorMessage("Please provide a shorter username (max length=" + MAX_USERNAME_LENGTH + ")");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            e.addErrorMessage("Please provide a shorter password (max length=" + MAX_PASSWORD_LENGTH + ")");
        }

        if (e.hasErrorMessages()) throw e;
    }

    @Override
    public void postValidate() throws BusinessLogicException {
        BusinessLogicException e = new BusinessLogicException(HttpStatus.BAD_REQUEST);

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            e.addErrorMessage("Username may only contain letters, numbers, '_' and '-'.");
        }

        if (email != null && email.isBlank()) {
            e.addErrorMessage("If email is provided, it cannot be empty.");
        }

        if (e.hasErrorMessages()) throw e;
    }
}
