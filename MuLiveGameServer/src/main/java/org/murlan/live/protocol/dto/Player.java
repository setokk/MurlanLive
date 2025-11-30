package org.murlan.live.protocol.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.live.game.deck.Deck;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private long id;
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") private LocalDateTime creationDate;
    @JsonIgnore private String jwt;
    @JsonIgnore private Deck deck;

    public Player(long id) {
        this.id = id;
    }

    public Player(long id, String username, LocalDateTime creationDate, String jwt) {
        this.id = id;
        this.username = username;
        this.creationDate = creationDate;
        this.jwt = jwt;
    }

    @JsonIgnore
    public boolean isInvalid() {
        return jwt == null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
