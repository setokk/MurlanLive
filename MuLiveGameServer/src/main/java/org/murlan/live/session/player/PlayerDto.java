package org.murlan.live.session.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.murlan.live.game.deck.Deck;

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
    private String jwt;
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
}
