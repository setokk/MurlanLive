package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.um.model.pk.ScoreTotalPK;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "score_total")
public class ScoreTotalEntity {
    @EmbeddedId
    private ScoreTotalPK id;

    @Column(name = "is_winner", updatable = false, nullable = false)
    private Boolean isWinner;

    @Column(name = "score", updatable = false, nullable = false)
    private Short score;
}
