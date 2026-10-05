package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdatePlayerInfoRequest {
    @NotNull(message = "[UpdatePlayerInfoRequest]: profileIconId is mandatory")
    private final Long profileIconId;

    @JsonCreator
    public UpdatePlayerInfoRequest(@JsonProperty("profileIconId") Long profileIconId) {
        this.profileIconId = profileIconId;
    }
}
