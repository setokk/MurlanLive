package org.murlan.um.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room")
public class RoomEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @Column(name = "is_public", updatable = false, nullable = false)
    private Boolean isPublic;

    @Column(name = "total_score_to_win", updatable = false, nullable = false)
    private Short totalScoreToWin;

    @Column(name = "rounds_played", updatable = false, nullable = false)
    private Short roundsPlayed;

    @Column(name = "creation_date", updatable = false, nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "finished_date", updatable = false, nullable = false)
    private LocalDateTime finishedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_player_id", nullable = false)
    private PlayerEntity owner;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomPlayerEntity> roomPlayers = new ArrayList<>();
}
