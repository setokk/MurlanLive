package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.um.model.pk.RoomPlayerId;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room_player")
public class RoomPlayerEntity {
    @EmbeddedId
    private RoomPlayerId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roomId")
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("playerId")
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerEntity player;

    @Column(name = "placement_index", nullable = false)
    private Short placementIndex;

    @Column(name = "total_score", nullable = false)
    private Short totalScore;
}
