package org.murlan.um.model.pk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class RoomPlayerId implements Serializable {
    @Column(name = "room_id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID roomId;

    @Column(name = "player_id", updatable = false, nullable = false)
    private Long playerId;
}
