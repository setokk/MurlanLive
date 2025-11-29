package org.murlan.um.model.pk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ScorePK {
    @Column(name = "player_id", updatable = false, nullable = false)
    private Long playerId;
    @Column(name = "game_state_id", updatable = false, nullable = false)
    private Long gameStateId;
}
