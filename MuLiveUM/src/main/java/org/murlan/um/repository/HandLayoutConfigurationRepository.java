package org.murlan.um.repository;

import org.murlan.um.model.HandLayoutConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HandLayoutConfigurationRepository extends JpaRepository<HandLayoutConfigurationEntity, Long> {
    @Query(value = "SELECT hlc FROM HandLayoutConfigurationEntity hlc WHERE hlc.player.id=:playerId")
    Optional<HandLayoutConfigurationEntity> findByPlayerId(@Param("playerId") Long playerId);
}
