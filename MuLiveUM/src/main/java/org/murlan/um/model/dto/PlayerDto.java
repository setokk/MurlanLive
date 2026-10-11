package org.murlan.um.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.um.core.ranking.Rank;
import org.murlan.um.core.ranking.Rating;
import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.PlayerRatingEntity;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class PlayerDto {
    private Long id;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime creationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    private long profileIconId;
    private Short rankOrdinal;
    private Integer displayRating;

    public static PlayerDto fromPlayer(PlayerEntity player) {
        return fromPlayer(player, player.getEmail());
    }

    public static PlayerDto fromPlayer(PlayerEntity player, String email) {
        Short rankOrdinal = null;
        Integer displayRating = null;

        PlayerRatingEntity playerRating = player.getRating();
        if (playerRating != null) {
            Rating rating = playerRating.toRating();
            rankOrdinal = (short) Rank.of(rating).ordinal();
            displayRating = rating.displayRating();
        }

        return new PlayerDto(
                player.getId(),
                player.getUsername(),
                player.getCreatedDate(),
                email,
                player.getProfileIcon().getId(),
                rankOrdinal,
                displayRating
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerDto playerDto)) return false;
        return Objects.equals(id, playerDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
