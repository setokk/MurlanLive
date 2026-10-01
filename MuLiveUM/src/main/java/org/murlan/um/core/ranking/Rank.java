package org.murlan.um.core.ranking;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Rank {
    BRONZE(Double.NEGATIVE_INFINITY),
    SILVER(4),
    GOLD(10),
    PLATINUM(20),
    DIAMOND(35),
    MASTER(50);

    public final double minOrdinal;
    public static final int MIN_MATCHES_FOR_TOP_TIERS = 15;

    public static Rank of(Rating r) {
        Rank result = BRONZE;
        for (Rank t : values()) {
            if (r.ordinal() >= t.minOrdinal) result = t;
        }

        if (r.matches < MIN_MATCHES_FOR_TOP_TIERS && result.compareTo(GOLD) > 0) {
            result = GOLD;
        }
        return result;
    }
}
