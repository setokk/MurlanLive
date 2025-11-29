package org.murlan.um.repository;

import org.murlan.um.model.ScoreTotalEntity;
import org.murlan.um.model.pk.ScorePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreTotalRepository extends JpaRepository<ScoreTotalEntity, ScorePK> {
}
