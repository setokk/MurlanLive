package org.murlan.um.repository;

import jakarta.persistence.LockModeType;
import org.murlan.um.model.PlayerRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PlayerRatingRepository extends JpaRepository<PlayerRatingEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM PlayerRatingEntity r WHERE r.playerId IN :ids ORDER BY r.playerId")
    List<PlayerRatingEntity> findAllForUpdate(@Param("ids") Collection<Long> ids);
}
