package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room")
public class RoomEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @Column(name = "is_public", updatable = false, nullable = false)
    private Boolean isPublic;

    @Column(name = "total_score_to_win", updatable = false, nullable = false)
    private Short totalScoreToWin;

    @Column(name = "creation_date", updatable = false, nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "finished_date", updatable = false, nullable = false)
    private LocalDateTime finishedDate;

    @Column(name = "num_players", updatable = false, nullable = false)
    private Short numPlayers;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<GameStateEntity> gameStates;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_player_id")
    private PlayerEntity owner;
}
