package org.murlan.um.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.murlan.um.core.ranking.Rating;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "player_rating")
public class PlayerRatingEntity {
    @Id
    @Column(name = "player_id")
    private Long playerId;

    @Column(name = "mu", nullable = false)
    private double mu;

    @Column(name = "sigma", nullable = false)
    private double sigma;

    @Column(name = "rooms_played", nullable = false)
    private int roomsPlayed;

    public static PlayerRatingEntity newFor(Long playerId) {
        Rating fresh = new Rating();
        return new PlayerRatingEntity(playerId, fresh.mu, fresh.sigma, 0);
    }

    public Rating toRating() {
        return new Rating(mu, sigma, roomsPlayed);
    }

    public void copyFrom(Rating r) {
        this.mu = r.mu;
        this.sigma = r.sigma;
        this.roomsPlayed = r.roomsPlayed;
    }
}
