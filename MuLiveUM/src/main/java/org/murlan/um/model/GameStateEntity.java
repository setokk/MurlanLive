package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.um.core.logic.GameStateEnum;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "game_state")
public class GameStateEntity {
    @Id
    @SequenceGenerator(
            name = "gameStateSeqGen",
            sequenceName = "game_state_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "gameStateSeqGen"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Enumerated(EnumType.ORDINAL)
    private GameStateEnum state;

    @OneToMany(mappedBy = "gameState", fetch = FetchType.LAZY)
    private List<ScoreEntity> scores;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomEntity room;
}
