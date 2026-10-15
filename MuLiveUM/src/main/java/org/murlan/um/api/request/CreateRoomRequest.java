package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.validation.IRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class CreateRoomRequest implements IRequest {
    @NotNull(message = "[CreateRoomRequest]: id cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: id cannot be empty")
    private final String id;

    @NotNull(message = "[CreateRoomRequest]: name cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: name cannot be empty")
    private final String name;

    @NotNull(message = "[CreateRoomRequest]: isPublic cannot be null")
    private final Boolean isPublic;

    @NotNull(message = "[CreateRoomRequest]: creationDate cannot be null")
    private final LocalDateTime creationDate;

    @NotNull(message = "[CreateRoomRequest]: totalScoreToWin cannot be null")
    private final Short totalScoreToWin;

    @NotNull(message = "[CreateRoomRequest]: turnDurationInSeconds cannot be null")
    private final Short turnDurationInSeconds;

    @NotNull(message = "[CreateRoomRequest]: roundsPlayed cannot be null")
    private final Short roundsPlayed;

    @NotNull(message = "[CreateRoomRequest]: totalScores cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: totalScores cannot be empty")
    private final Map<Long, Short> totalScores;

    @NotNull(message = "[CreateRoomRequest]: owner cannot be null")
    private final Long ownerId;

    @JsonCreator
    public CreateRoomRequest(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("isPublic") Boolean isPublic,
            @JsonProperty("creationDate") LocalDateTime creationDate,
            @JsonProperty("totalScoreToWin") Short totalScoreToWin,
            @JsonProperty("turnDurationInSeconds") Short turnDurationInSeconds,
            @JsonProperty("roundsPlayed") Short roundsPlayed,
            @JsonProperty("totalScores") Map<Long, Short> totalScores,
            @JsonProperty("ownerId") Long ownerId
    ) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.creationDate = creationDate;
        this.totalScoreToWin = totalScoreToWin;
        this.turnDurationInSeconds = turnDurationInSeconds;
        this.roundsPlayed = roundsPlayed;
        this.totalScores = totalScores;
        this.ownerId = ownerId;
    }
}
