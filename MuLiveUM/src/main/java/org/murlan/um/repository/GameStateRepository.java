package org.murlan.um.repository;

import org.murlan.um.model.GameStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStateRepository extends JpaRepository<GameStateEntity, Long> {

}
