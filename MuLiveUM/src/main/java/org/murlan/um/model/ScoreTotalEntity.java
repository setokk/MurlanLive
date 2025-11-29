package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.um.model.pk.ScorePK;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "score_total")
public class ScoreTotalEntity {
    @EmbeddedId
    private ScorePK id;

    @Column(name = "score", updatable = false, nullable = false)
    private Short score;
}
