package org.murlan.um.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public final class PlayerDto {
    private Long id;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") private LocalDateTime creationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

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
