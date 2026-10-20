package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

@Getter
public class CheckHandLayoutConfigurationRequest implements IRequest {
    @NotNull(message = "[CheckHandLayoutConfigurationRequest]: sha field is mandatory")
    private String sha;

    @JsonCreator
    public CheckHandLayoutConfigurationRequest(@JsonProperty("sha") String sha) {
        this.sha = sha;
    }
}
