package org.murlan.um.repository;

import org.murlan.um.model.PlayerResetPasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlayerResetPasswordEntityRepository extends JpaRepository<PlayerResetPasswordEntity, Long> {
    @Query(value = "select prp from PlayerResetPasswordEntity prp where prp.token=:token")
    Optional<PlayerResetPasswordEntity> findByToken(@Param("token") String token);
}
