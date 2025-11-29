package org.murlan.um.repository;

import org.murlan.um.model.HistoryEntity;
import org.murlan.um.model.pk.HistoryPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<HistoryEntity, HistoryPK> {
    @Query(value = "SELECT h FROM HistoryEntity h WHERE h.id.roomId=:roomId")
    List<HistoryEntity> findHistoriesByRoomId(@Param("roomId") String roomId);
}
