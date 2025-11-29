package org.murlan.um.repository;

import org.murlan.um.model.ScoreEntity;
import org.murlan.um.model.pk.ScorePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<ScoreEntity, ScorePK> {
}
