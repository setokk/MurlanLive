package org.murlan.um.repository;

import org.murlan.um.model.PlayerEmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlayerEmailVerificationRepository extends JpaRepository<PlayerEmailVerificationEntity, Long> {
    @Query(value = "select pev from PlayerEmailVerificationEntity pev where pev.token=:token")
    Optional<PlayerEmailVerificationEntity> findByToken(@Param("token") String token);
}
