package org.murlan.um.repository;

import org.murlan.um.model.RoomEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    @Query(
    """
        select distinct r
        from RoomEntity r
        join r.totalScores ts
        where ts.id.playerId = :playerId
    """)
    List<RoomEntity> findRoomsByPlayerId(@Param("playerId") Long playerId, Pageable pageable);
}
