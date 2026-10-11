package org.murlan.um.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class RankRatingDto {
    private short previousRank;
    private short newRank;
    private int previousRating;
    private int newRating;
}
