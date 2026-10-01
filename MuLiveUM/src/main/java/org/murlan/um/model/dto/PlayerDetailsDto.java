package org.murlan.um.model.dto;

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
    private LocalDateTime creationDate;
    private String email;
    private Object rank;

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
