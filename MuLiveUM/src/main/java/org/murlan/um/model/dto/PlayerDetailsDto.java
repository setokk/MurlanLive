package org.murlan.um.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class PlayerDetailsDto {
    private Long id;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime creationDate;
    private String email;
    private long profileIconId;
    private String rank;
    private Integer rating;
    private long totalRoomsPlayed;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerDetailsDto playerDetailsDto)) return false;
        return Objects.equals(id, playerDetailsDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
