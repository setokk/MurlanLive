package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

@Getter
public class BlockPlayerRequest implements IRequest {
    @NotNull(message = "[BlockPlayerRequest]: playerToBlockId cannot be null")
    @PositiveOrZero(message = "[BlockPlayerRequest]: playerToBlockId cannot be negative")
    private final Long playerToBlockId;

    @JsonCreator
    public BlockPlayerRequest(@JsonProperty("playerToBlockId") Long playerToBlockId) {
        this.playerToBlockId = playerToBlockId;
    }
}
