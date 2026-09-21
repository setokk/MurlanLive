package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

@Getter
public class UnblockPlayerRequest implements IRequest {
    @NotNull(message = "[UnblockPlayerRequest]: playerToUnblockId cannot be null")
    @PositiveOrZero(message = "[UnblockPlayerRequest]: playerToUnblockId cannot be negative")
    private final Long playerToUnblockId;

    @JsonCreator
    public UnblockPlayerRequest(@JsonProperty("playerToUnblockId") Long playerToUnblockId) {
        this.playerToUnblockId = playerToUnblockId;
    }
}
