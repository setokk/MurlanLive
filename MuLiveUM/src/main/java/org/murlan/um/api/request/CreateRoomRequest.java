package org.murlan.um.api.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.murlan.um.api.dto.PlayerDto;
import org.murlan.um.api.dto.GameStateDto;
import org.murlan.um.api.request.validation.IRequest;
import org.murlan.um.error.BusinessLogicException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
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

    @NotNull(message = "[CreateRoomRequest]: isPublic cannot be null")
    private final Short totalScoreToWin;

    @NotNull(message = "[CreateRoomRequest]: gameStates cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: gameStates cannot be empty")
    private final List<GameStateDto> gameStates;

    @NotNull(message = "[CreateRoomRequest]: totalScores cannot be null")
    @NotEmpty(message = "[CreateRoomRequest]: totalScores cannot be empty")
    private final Map<String, Short> totalScores;

    @NotNull(message = "[CreateRoomRequest]: numPlayers cannot be null")
    private final Short numPlayers;

    @NotNull(message = "[CreateRoomRequest]: owner cannot be null")
    private final PlayerDto owner;

    @Override
    public void preValidate() throws BusinessLogicException {
        BusinessLogicException e = new BusinessLogicException(HttpStatus.BAD_REQUEST);

        if (owner.getId() == null) {
            e.addErrorMessage("[CreateRoomRequest]: owner.id cannot be null");
        }
        if (owner.getUsername() == null) {
            e.addErrorMessage("[CreateRoomRequest]: owner.username cannot be null");
        }
        if (owner.getCreationDate() == null) {
            e.addErrorMessage("[CreateRoomRequest]: owner.creationDate cannot be null");
        }

        if (e.hasErrorMessages()) throw e;
    }

    @JsonCreator
    public CreateRoomRequest(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("isPublic") Boolean isPublic,
            @JsonProperty("creationDate") LocalDateTime creationDate,
            @JsonProperty("totalScoreToWin") Short totalScoreToWin,
            @JsonProperty("gameStates") List<GameStateDto> gameStates,
            @JsonProperty("totalScores") Map<String, Short> totalScores,
            @JsonProperty("numPlayers") Short numPlayers,
            @JsonProperty("owner") PlayerDto owner
    ) {
        this.id = id;
        this.name = name;
        this.isPublic = isPublic;
        this.creationDate = creationDate;
        this.totalScoreToWin = totalScoreToWin;
        this.gameStates = gameStates;
        this.totalScores = totalScores;
        this.numPlayers = numPlayers;
        this.owner = owner;
    }
}
