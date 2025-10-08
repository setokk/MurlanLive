package org.murlan.live.protocol.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.murlan.live.game.deck.Deck;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PlayerDto {
    private long id;
    private String username;
    private String creationDate;
    @JsonIgnore
    private String jwt;
    @JsonIgnore
    private Deck deck;

    public PlayerDto(long id, String username, String creationDate, String jwt) {
        this.id = id;
        this.username = username;
        this.creationDate = creationDate;
        this.jwt = jwt;
    }

    public boolean isInvalid() {
        return jwt == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerDto playerDto = (PlayerDto) o;
        return id == playerDto.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
