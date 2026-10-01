package org.murlan.um.core.ranking;

import static org.murlan.um.core.ranking.RankingSystem.MU0;
import static org.murlan.um.core.ranking.RankingSystem.SIGMA0;

public final class Rating {
    public double mu = MU0;
    public double sigma = SIGMA0;
    public int matches = 0;

    /** Conservative skill estimate. Starts at 0 and rises as sigma shrinks. */
    public double ordinal() {
        return mu - 3.0 * sigma;
    }

    @Override
    public String toString() {
        return String.format("mu=%.2f sigma=%.2f ordinal=%.2f matches=%d",
                mu, sigma, ordinal(), matches);
    }
}
