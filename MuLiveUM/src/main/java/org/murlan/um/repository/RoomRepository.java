package org.murlan.um.repository;

import org.murlan.um.model.RoomEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {
    @Query("""
        SELECT DISTINCT r
        FROM RoomEntity r
        JOIN r.roomPlayers rp
        WHERE rp.player.id = :playerId
    """)
    List<RoomEntity> findAllRoomsByPlayerId(@Param("playerId") Long playerId, Pageable pageable);

    @Query("""
        SELECT DISTINCT r
        FROM RoomEntity r
        JOIN r.roomPlayers rp
        WHERE r.isPublic = true AND rp.player.id = :playerId
    """)
    List<RoomEntity> findPublicRoomsByPlayerId(@Param("playerId") Long playerId, Pageable pageable);
}