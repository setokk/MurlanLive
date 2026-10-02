package org.murlan.um.service;

import lombok.RequiredArgsConstructor;
import org.murlan.um.core.ranking.Rank;
import org.murlan.um.core.ranking.RankingSystem;
import org.murlan.um.core.ranking.Rating;
import org.murlan.um.model.PlayerRatingEntity;
import org.murlan.um.model.dto.RankRatingDto;
import org.murlan.um.repository.PlayerRatingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerRatingService {
    private final PlayerRatingRepository ratingRepository;

    public Map<Long, RankRatingDto> updatePlayerRatings(
            Map<Long, Short> pointsByPlayerId,
            int totalScoreToWin
    ) {
        List<Long> ids = pointsByPlayerId.keySet().stream().sorted().toList();

        Map<Long, PlayerRatingEntity> entities = ratingRepository.findAllForUpdate(ids).stream()
                .collect(Collectors.toMap(
                        PlayerRatingEntity::getPlayerId,
                        Function.identity()
                ));

        for (Long id : ids) {
            entities.computeIfAbsent(id, PlayerRatingEntity::newFor);
        }

        List<Rating> ratings = new ArrayList<>(ids.size());
        List<Rating> previousRatings = new ArrayList<>(ids.size());
        double[] points = new double[ids.size()];

        for (int i = 0; i < ids.size(); i++) {
            Rating rating = entities.get(ids.get(i)).toRating();

            previousRatings.add(rating.copy());
            ratings.add(rating);
            points[i] = pointsByPlayerId.get(ids.get(i));
        }

        int[] ranks = RankingSystem.ranksFromPoints(points, true);
        RankingSystem.updateMatch(ratings, ranks, totalScoreToWin);

        Map<Long, RankRatingDto> result = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            Long playerId = ids.get(i);

            Rating previous = previousRatings.get(i);
            Rating current = ratings.get(i);

            PlayerRatingEntity entity = entities.get(playerId);
            entity.copyFrom(current);
            ratingRepository.save(entity);

            result.put(playerId, RankRatingDto.builder()
                    .previousRank(Rank.of(previous).displayName())
                    .newRank(Rank.of(current).displayName())
                    .previousRating(previous.displayRating())
                    .newRating(current.displayRating())
                    .build());
        }

        return result;
    }
}
