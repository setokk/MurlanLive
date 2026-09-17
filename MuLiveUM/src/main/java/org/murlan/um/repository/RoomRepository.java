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
        SELECT DISTINCT r
        FROM RoomEntity r
        JOIN r.totalScores ts
        WHERE ts.id.playerId=:playerId
    """)
    List<RoomEntity> findAllRoomsByPlayerId(@Param("playerId") Long playerId, Pageable pageable);

    @Query(
    """
        SELECT DISTINCT r
        FROM RoomEntity r
        JOIN r.totalScores ts
        WHERE r.isPublic=true AND ts.id.playerId=:playerId
    """)
    List<RoomEntity> findPublicRoomsByPlayerId(@Param("playerId") Long playerId, Pageable pageable);
}
