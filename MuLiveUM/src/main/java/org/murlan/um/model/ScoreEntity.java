package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.um.model.pk.ScorePK;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "score")
public class ScoreEntity {
    @EmbeddedId
    private ScorePK id;

    @Column(name = "score", updatable = false, nullable = false)
    private Short score;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameStateId")
    @JoinColumn(name = "game_state_id")
    private GameStateEntity gameState;
}
