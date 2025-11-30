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
public class ScoreTotalPK {
    @Column(name = "player_id", updatable = false, nullable = false)
    private Long playerId;
    @Column(name = "room_id", updatable = false, nullable = false)
    private String roomId;
}
