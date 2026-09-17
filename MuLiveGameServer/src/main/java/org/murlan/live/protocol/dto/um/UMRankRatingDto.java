package org.murlan.live.protocol.dto.um;

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
public final class UMRankRatingDto {
    private String previousRank;
    private String newRank;
    private int previousRating;
    private int newRating;
}
