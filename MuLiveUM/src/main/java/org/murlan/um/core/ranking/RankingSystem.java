package org.murlan.um.core.ranking;

import java.util.List;

/**
 * OpenSkill (Plackett-Luce) rating for free-for-all Murlan matches.
 * <br/>
 * - One rating update per MATCH (room), using final cumulative points.
 * - Beta (performance noise) is raised globally for luck, and scaled per match
 *   by total_max_score: short matches are noisier, long matches are more reliable.
 * - Works for 2..N players and supports tied ranks.
 */
public final class RankingSystem {

    /* ---- OpenSkill defaults ---- */
    static final double MU0 = 25.0;
    static final double SIGMA0 = MU0 / 3.0;      // 8.333
    static final double BASE_BETA = MU0 / 6.0;   // 4.167
    static final double TAU = MU0 / 300.0;       // 0.0833 (skill drift per match)
    static final double KAPPA = 0.0001;          // floor so sigma never collapses

    /* ---- Luck tuning ---- */
    static final double LUCK_FACTOR = 1.5;       // global beta multiplier for luck
    static final double REF_MAX_SCORE = 10.0;    // match length where scale == 1
    static final double MIN_SCALE = 0.5;         // clamp for very long matches
    static final double MAX_SCALE = 2.0;         // clamp for very short matches

    private RankingSystem() {}

    /**
     * Converts final cumulative points into ranks (1 = best). Ties share a rank.
     * Set higherIsBetter=false if, in your scoring, fewer points means a better finish.
     */
    public static int[] ranksFromPoints(double[] points, boolean higherIsBetter) {
        int n = points.length;
        int[] ranks = new int[n];
        for (int i = 0; i < n; i++) {
            int better = 0;
            for (double point : points) {
                if (higherIsBetter ? point > points[i] : point < points[i]) {
                    better++;
                }
            }
            ranks[i] = better + 1;
        }
        return ranks;
    }

    /**
     * Beta for this match. Averaging over k hands shrinks luck noise by ~sqrt(k),
     * and the number of hands grows with total_max_score, so:
     * beta = BASE_BETA * LUCK_FACTOR * sqrt(REF / totalMaxScore), clamped.
     */
    public static double betaFor(int totalMaxScore) {
        double scale = Math.sqrt(REF_MAX_SCORE / Math.max(1, totalMaxScore));
        scale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);
        return BASE_BETA * LUCK_FACTOR * scale;
    }

    /**
     * Updates all players' ratings in place after one finished match.
     *
     * @param players       ratings, same order as ranks
     * @param ranks         final rank per player (1 = best, equal numbers = tie)
     * @param totalMaxScore the room's total_max_score setting (3..50)
     */
    public static void updateMatch(List<Rating> players, int[] ranks, int totalMaxScore) {
        int n = players.size();
        if (n < 2 || ranks.length != n) {
            throw new IllegalArgumentException("Need >= 2 players and one rank per player");
        }

        double beta = betaFor(totalMaxScore);
        double[] mu = new double[n];
        double[] sigSq = new double[n];
        double c2 = 0;
        for (int i = 0; i < n; i++) {
            Rating r = players.get(i);
            mu[i] = r.mu;
            sigSq[i] = r.sigma * r.sigma + TAU * TAU;   // let skill drift a little
            c2 += sigSq[i] + beta * beta;
        }
        double c = Math.sqrt(c2);

        double[] expMu = new double[n];
        for (int i = 0; i < n; i++) expMu[i] = Math.exp(mu[i] / c);

        // sumQ[q]: total strength of everyone who finished at q's rank or worse
        // a[q]:    number of players tied at q's rank
        double[] sumQ = new double[n];
        int[] a = new int[n];
        for (int q = 0; q < n; q++) {
            for (int i = 0; i < n; i++) {
                if (ranks[i] >= ranks[q]) sumQ[q] += expMu[i];
                if (ranks[i] == ranks[q]) a[q]++;
            }
        }

        double[] newMu = new double[n];
        double[] newSigma = new double[n];
        for (int i = 0; i < n; i++) {
            double omega = 0;
            double delta = 0;
            for (int q = 0; q < n; q++) {
                if (ranks[q] > ranks[i]) continue;      // only players ahead of / tied with i
                double p = expMu[i] / sumQ[q];
                delta += p * (1 - p) / a[q];
                omega += (q == i ? (1 - p) : -p) / a[q];
            }
            double gamma = Math.sqrt(sigSq[i]) / c;
            omega *= sigSq[i] / c;
            delta *= gamma * sigSq[i] / c2;

            newMu[i] = mu[i] + omega;
            newSigma[i] = Math.sqrt(sigSq[i] * Math.max(1 - delta, KAPPA));
        }

        for (int i = 0; i < n; i++) {
            Rating r = players.get(i);
            r.mu = newMu[i];
            r.sigma = newSigma[i];
            r.roomsPlayed++;
        }
    }
}