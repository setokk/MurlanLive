package org.murlan.um.repository;

import org.murlan.um.model.PlayerEntity;
import org.murlan.um.model.view.PlayerDetailsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {
    @Query(value = "SELECT p FROM PlayerEntity p WHERE p.username=:username")
    Optional<PlayerEntity> findPlayerByUsername(@Param("username") String username);

    @Query(value = "SELECT p FROM PlayerEntity p WHERE p.email=:email")
    Optional<PlayerEntity> findPlayerByEmail(@Param("email") String email);

    @Query(value = "SELECT p FROM PlayerEntity p WHERE p.username=:usernameOrEmail OR p.email=:usernameOrEmail")
    Optional<PlayerEntity> findPlayerByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    @Query("""
            SELECT p.id AS id,
                   p.username AS username,
                   p.createdDate AS creationDate,
                   CASE WHEN p.id = :requestingPlayerId THEN p.email ELSE NULL END AS email,
                   p.profileIcon.id AS profileIconId,
                   pr.mu AS mu,
                   pr.sigma AS sigma,
                   pr.roomsPlayed AS roomsPlayed,
                   (SELECT COUNT(rp) FROM RoomPlayerEntity rp WHERE rp.player = p AND rp.placementIndex = 1) AS totalWins
            FROM PlayerEntity p
            LEFT JOIN p.rating pr
            WHERE p.id = :playerId
            """)
    Optional<PlayerDetailsView> findPlayerDetails(@Param("playerId") long playerId, @Param("requestingPlayerId") long requestingPlayerId);
}
