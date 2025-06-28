package org.murlan.um.repository;

import org.murlan.um.model.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    @Query(value = "SELECT p FROM PlayerEntity p WHERE p.username=:username")
    Optional<PlayerEntity> findPlayerByUsername(@Param("username") String username);
}
