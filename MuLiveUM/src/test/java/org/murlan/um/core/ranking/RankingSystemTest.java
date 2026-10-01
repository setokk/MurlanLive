package org.murlan.um.core.ranking;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.function.ToIntFunction;
import org.junit.jupiter.api.Test;

/**
 * Simulation harness for RankingSystem (OpenSkill / Plackett-Luce).
 *
 * How the simulated world works:
 * - A pool of 100 players, each with a HIDDEN true skill ~ N(0, SKILL_SD) and an
 *   activity weight (log-normal), so some players play far more rooms than others.
 * - Every simulation is a fresh start: it takes numPlayers of the pool, all ratings reset.
 * - A room = 4 players picked by activity weight, then game states (hands) are played:
 *   each hand, performance = skill + N(0, luckSd); best performance gets 3 points,
 *   then 2, 1, 0. The room ends when someone's total reaches total_max_score.
 *   Final standings = points desc, ties broken by who reached their total first
 *   (Murlan Live decides this in production; this only imitates it).
 * - Ratings are updated ONCE per room with ranks {1,2,3,4} in standing order.
 *
 * Because we know the hidden skill, we can measure how well the ratings recover it.
 * Run: mvn test -Dtest=RankingSystemTest   (results are printed to the console)
 *
 * "Gain per match" = change of Rating.ordinal() caused by one match, grouped by the
 * player's Rank at the START of that match (so a player who is Gold before the match
 * counts as Gold, even if the match promotes or demotes them).
 */
class RankingSystemTest {

    /* ---- Simulated world (assumptions, change freely) ---- */
    private static final int POOL_SIZE = 100;
    private static final double SKILL_SD = 1.0;        // spread of true skill
    private static final double DEFAULT_LUCK_SD = 1.5; // per-hand luck; bigger = more luck
    private static final double ACTIVITY_SD = 1.0;     // spread of how often players play
    private static final int[] HAND_POINTS = {3, 2, 1, 0};
    private static final int[] MIXED_SCORES = {3, 5, 7, 11, 15, 21, 31, 41, 50};

    private static ToIntFunction<Random> fixed(int score) {
        return r -> score;
    }

    private static final ToIntFunction<Random> MIXED =
            r -> MIXED_SCORES[r.nextInt(MIXED_SCORES.length)];

    record Scenario(String name, int players, int rooms,
                    ToIntFunction<Random> scores, double luckSd) {}

    record Metrics(int rated, double rhoOrd, double rhoMu, double topOverlap,
                   double meanMu, double meanSigma, double meanOrd, double maxOrd,
                   int[] tierCount, double[] tierSkill) {}

    /* ====================================================================== */
    /* Gain statistics                                                         */
    /* ====================================================================== */

    /** Accumulates per-match rating changes, grouped by the rank held BEFORE the match. */
    static final class GainStats {
        static final int R = Rank.values().length;

        final long[] n = new long[R];
        final long[] up = new long[R];              // matches with ordinal gain > 0
        final double[] ord = new double[R];         // sum of ordinal change
        final double[] mu = new double[R];          // sum of mu change
        final double[] sigma = new double[R];       // sum of sigma change
        final double[][] posOrd = new double[R][4]; // sum of ordinal change by finishing position
        final long[][] posN = new long[R][4];

        void add(Rank rank, int position, double dOrd, double dMu, double dSigma) {
            int r = rank.ordinal();
            n[r]++;
            if (dOrd > 0) up[r]++;
            ord[r] += dOrd;
            mu[r] += dMu;
            sigma[r] += dSigma;
            posOrd[r][position] += dOrd;
            posN[r][position]++;
        }

        void merge(GainStats o) {
            for (int r = 0; r < R; r++) {
                n[r] += o.n[r];
                up[r] += o.up[r];
                ord[r] += o.ord[r];
                mu[r] += o.mu[r];
                sigma[r] += o.sigma[r];
                for (int p = 0; p < 4; p++) {
                    posOrd[r][p] += o.posOrd[r][p];
                    posN[r][p] += o.posN[r][p];
                }
            }
        }

        long total() {
            long t = 0;
            for (long v : n) t += v;
            return t;
        }

        double avgOrd(int r) {
            return n[r] == 0 ? Double.NaN : ord[r] / n[r];
        }

        double avgOrdAll() {
            long t = total();
            double s = 0;
            for (double v : ord) s += v;
            return t == 0 ? Double.NaN : s / t;
        }
    }

    /* ====================================================================== */
    /* Simulation                                                              */
    /* ====================================================================== */

    static final class Sim {
        final Random rnd;
        final int n;
        final double[] skill;
        final double[] activity;
        final List<Rating> ratings = new ArrayList<>();
        final double luckSd;
        final int warmupRooms;
        int roomsPlayed;

        /** finish[0] = highest-mu player in room, [1] = lowest-mu, [2] = truly best (oracle). */
        final long[][] finish = new long[3][4];
        long statRooms;

        /** Gain per match by rank: over all matches, and over the 2nd half only (after warmup). */
        final GainStats gainAll = new GainStats();
        final GainStats gainLate = new GainStats();

        Sim(long seed, int numPlayers, double luckSd, int warmupRooms) {
            if (numPlayers < 4 || numPlayers > POOL_SIZE) {
                throw new IllegalArgumentException("numPlayers must be 4.." + POOL_SIZE);
            }
            this.rnd = new Random(seed);
            this.n = numPlayers;
            this.luckSd = luckSd;
            this.warmupRooms = warmupRooms;

            double[] poolSkill = new double[POOL_SIZE];
            double[] poolActivity = new double[POOL_SIZE];
            for (int i = 0; i < POOL_SIZE; i++) {
                poolSkill[i] = rnd.nextGaussian() * SKILL_SD;
                poolActivity[i] = Math.exp(rnd.nextGaussian() * ACTIVITY_SD);
            }
            List<Integer> order = new ArrayList<>();
            for (int i = 0; i < POOL_SIZE; i++) order.add(i);
            Collections.shuffle(order, rnd);

            skill = new double[n];
            activity = new double[n];
            for (int i = 0; i < n; i++) {
                int p = order.get(i);
                skill[i] = poolSkill[p];
                activity[i] = poolActivity[p];
                ratings.add(new Rating());
            }
        }

        /** Picks 4 distinct players, weighted by activity. */
        private int[] pickRoom() {
            int[] room = new int[4];
            boolean[] used = new boolean[n];
            for (int k = 0; k < 4; k++) {
                double total = 0;
                for (int i = 0; i < n; i++) if (!used[i]) total += activity[i];
                double x = rnd.nextDouble() * total;
                int chosen = -1;
                int lastFree = -1;
                for (int i = 0; i < n; i++) {
                    if (used[i]) continue;
                    lastFree = i;
                    x -= activity[i];
                    if (x <= 0) {
                        chosen = i;
                        break;
                    }
                }
                if (chosen < 0) chosen = lastFree;
                used[chosen] = true;
                room[k] = chosen;
            }
            return room;
        }

        /** Plays game states until someone reaches maxScore; returns player indices 1st..4th. */
        private int[] standings(int[] room, int maxScore) {
            int[] points = new int[4];
            int[] reachedAt = new int[4];
            int hand = 0;
            while (Arrays.stream(points).max().getAsInt() < maxScore) {
                hand++;
                double[] perf = new double[4];
                for (int j = 0; j < 4; j++) {
                    perf[j] = skill[room[j]] + luckSd * rnd.nextGaussian();
                }
                Integer[] byPerf = {0, 1, 2, 3};
                Arrays.sort(byPerf, (a, b) -> Double.compare(perf[b], perf[a]));
                for (int k = 0; k < 4; k++) {
                    if (HAND_POINTS[k] > 0) {
                        points[byPerf[k]] += HAND_POINTS[k];
                        reachedAt[byPerf[k]] = hand;
                    }
                }
            }
            Integer[] order = {0, 1, 2, 3};
            Arrays.sort(order, (a, b) -> points[a] != points[b]
                    ? Integer.compare(points[b], points[a])
                    : Integer.compare(reachedAt[a], reachedAt[b]));
            int[] result = new int[4];
            for (int k = 0; k < 4; k++) result[k] = room[order[k]];
            return result;
        }

        void playRoom(int maxScore) {
            int[] room = pickRoom();
            int[] order = standings(room, maxScore);
            boolean late = roomsPlayed >= warmupRooms;
            if (late) recordFinish(room, order);

            List<Rating> ordered = new ArrayList<>(4);
            for (int idx : order) ordered.add(ratings.get(idx));

            // snapshot BEFORE the update: rank, ordinal, mu, sigma
            Rank[] rankBefore = new Rank[4];
            double[] ordBefore = new double[4];
            double[] muBefore = new double[4];
            double[] sigmaBefore = new double[4];
            for (int k = 0; k < 4; k++) {
                Rating r = ordered.get(k);
                rankBefore[k] = Rank.of(r);
                ordBefore[k] = r.ordinal();
                muBefore[k] = r.mu;
                sigmaBefore[k] = r.sigma;
            }

            RankingSystem.updateMatch(ordered, new int[]{1, 2, 3, 4}, maxScore);

            for (int k = 0; k < 4; k++) {
                Rating r = ordered.get(k);
                double dOrd = r.ordinal() - ordBefore[k];
                double dMu = r.mu - muBefore[k];
                double dSigma = r.sigma - sigmaBefore[k];
                gainAll.add(rankBefore[k], k, dOrd, dMu, dSigma);
                if (late) gainLate.add(rankBefore[k], k, dOrd, dMu, dSigma);
            }
            roomsPlayed++;
        }

        private void recordFinish(int[] room, int[] order) {
            int topMu = room[0];
            int lowMu = room[0];
            int topSkill = room[0];
            for (int idx : room) {
                if (ratings.get(idx).mu > ratings.get(topMu).mu) topMu = idx;
                if (ratings.get(idx).mu < ratings.get(lowMu).mu) lowMu = idx;
                if (skill[idx] > skill[topSkill]) topSkill = idx;
            }
            finish[0][position(order, topMu)]++;
            finish[1][position(order, lowMu)]++;
            finish[2][position(order, topSkill)]++;
            statRooms++;
        }

        private static int position(int[] order, int playerIdx) {
            for (int k = 0; k < order.length; k++) if (order[k] == playerIdx) return k;
            throw new IllegalStateException();
        }

        double avgGames() {
            double total = 0;
            for (Rating r : ratings) total += r.matches;
            return total / n;
        }

        /** Number of players (with at least 1 match) currently in each Rank, indexed by Rank ordinal. */
        int[] rankCounts() {
            int[] counts = new int[Rank.values().length];
            for (Rating r : ratings) {
                if (r.matches > 0) counts[Rank.of(r).ordinal()]++;
            }
            return counts;
        }

        int unplayedCount() {
            int c = 0;
            for (Rating r : ratings) if (r.matches == 0) c++;
            return c;
        }

        /** Players who played, but fewer than MIN_MATCHES_FOR_TOP_TIERS (their rank is capped at GOLD). */
        int provisionalCount() {
            int c = 0;
            for (Rating r : ratings) {
                if (r.matches > 0 && r.matches < Rank.MIN_MATCHES_FOR_TOP_TIERS) c++;
            }
            return c;
        }

        Metrics snapshot() {
            List<Integer> rated = new ArrayList<>();
            for (int i = 0; i < n; i++) if (ratings.get(i).matches > 0) rated.add(i);
            int m = rated.size();
            int tiers = Rank.values().length;
            int[] tierCount = new int[tiers];
            double[] tierSkill = new double[tiers];
            if (m == 0) {
                return new Metrics(0, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
                        Double.NaN, Double.NaN, Double.NaN, tierCount, tierSkill);
            }

            double[] sk = new double[m];
            double[] ord = new double[m];
            double[] mu = new double[m];
            double sumMu = 0, sumSigma = 0, sumOrd = 0;
            double maxOrd = Double.NEGATIVE_INFINITY;
            for (int k = 0; k < m; k++) {
                int i = rated.get(k);
                Rating r = ratings.get(i);
                sk[k] = skill[i];
                ord[k] = r.ordinal();
                mu[k] = r.mu;
                sumMu += r.mu;
                sumSigma += r.sigma;
                sumOrd += ord[k];
                maxOrd = Math.max(maxOrd, ord[k]);
                int t = Rank.of(r).ordinal();
                tierCount[t]++;
                tierSkill[t] += skill[i];
            }
            for (int t = 0; t < tiers; t++) {
                if (tierCount[t] > 0) tierSkill[t] /= tierCount[t];
            }

            int k = Math.max(1, m / 10);
            Set<Integer> overlap = topK(sk, k);
            overlap.retainAll(topK(ord, k));

            double[] skillRanks = averageRanks(sk);
            double rhoOrd = m >= 3 ? pearson(skillRanks, averageRanks(ord)) : Double.NaN;
            double rhoMu = m >= 3 ? pearson(skillRanks, averageRanks(mu)) : Double.NaN;
            return new Metrics(m, rhoOrd, rhoMu, (double) overlap.size() / k,
                    sumMu / m, sumSigma / m, sumOrd / m, maxOrd, tierCount, tierSkill);
        }
    }

    /* ====================================================================== */
    /* Statistics helpers                                                      */
    /* ====================================================================== */

    private static Set<Integer> topK(double[] v, int k) {
        Integer[] idx = new Integer[v.length];
        for (int i = 0; i < v.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(v[b], v[a]));
        return new HashSet<>(Arrays.asList(idx).subList(0, k));
    }

    private static double[] averageRanks(double[] v) {
        int m = v.length;
        Integer[] idx = new Integer[m];
        for (int i = 0; i < m; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(v[a], v[b]));
        double[] r = new double[m];
        int i = 0;
        while (i < m) {
            int j = i;
            while (j + 1 < m && v[idx[j + 1]] == v[idx[i]]) j++;
            double avg = (i + j) / 2.0 + 1;
            for (int k = i; k <= j; k++) r[idx[k]] = avg;
            i = j + 1;
        }
        return r;
    }

    private static double pearson(double[] a, double[] b) {
        int m = a.length;
        double ma = 0, mb = 0;
        for (int i = 0; i < m; i++) {
            ma += a[i];
            mb += b[i];
        }
        ma /= m;
        mb /= m;
        double cov = 0, va = 0, vb = 0;
        for (int i = 0; i < m; i++) {
            cov += (a[i] - ma) * (b[i] - mb);
            va += (a[i] - ma) * (a[i] - ma);
            vb += (b[i] - mb) * (b[i] - mb);
        }
        double den = Math.sqrt(va * vb);
        return den == 0 ? Double.NaN : cov / den;
    }

    /* ====================================================================== */
    /* Reporting                                                               */
    /* ====================================================================== */

    private static void out(String fmt, Object... args) {
        System.out.printf(Locale.ROOT, fmt, args);
    }

    private static String pct(Sim sim, int who, int position) {
        if (sim.statRooms == 0) return "   -";
        return String.format(Locale.ROOT, "%4.0f", 100.0 * sim.finish[who][position] / sim.statRooms);
    }

    private static String tierCell(int count, double avgSkill) {
        if (count == 0) return String.format(Locale.ROOT, "%4d(  -- )", 0);
        return String.format(Locale.ROOT, "%4d(%+5.2f)", count, avgSkill);
    }

    private static List<Sim> runAll(String title, List<Scenario> scenarios, long baseSeed) {
        List<Sim> sims = new ArrayList<>();
        for (int i = 0; i < scenarios.size(); i++) {
            Scenario s = scenarios.get(i);
            Sim sim = new Sim(baseSeed + i, s.players(), s.luckSd(), s.rooms() / 2);
            for (int r = 0; r < s.rooms(); r++) {
                sim.playRoom(s.scores().applyAsInt(sim.rnd));
            }
            sims.add(sim);
        }
        printReport(title, scenarios, sims);
        return sims;
    }

    /** One row per scenario, one column per rank: average ordinal gain per match. */
    private static void printGainByRank(String title, List<Scenario> scenarios, List<Sim> sims, boolean late) {
        out("%n-- %s --%n", title);
        out("%-30s %8s", "scenario", "all");
        for (Rank r : Rank.values()) out(" %9s", r.name());
        out("%n");
        for (int i = 0; i < scenarios.size(); i++) {
            GainStats g = late ? sims.get(i).gainLate : sims.get(i).gainAll;
            out("%-30s %+8.2f", scenarios.get(i).name(), g.avgOrdAll());
            for (int r = 0; r < GainStats.R; r++) {
                out(" %9s", g.n[r] == 0 ? "--" : String.format(Locale.ROOT, "%+.2f", g.avgOrd(r)));
            }
            out("%n");
        }
    }

    /** Detailed table for one GainStats: per rank, then split by finishing position. */
    private static void printGainDetail(String title, GainStats g) {
        out("%n-- %s --%n", title);
        out("%-9s %9s %8s %8s %8s %6s | %7s %7s %7s %7s%n",
                "rank", "matches", "dOrd", "dMu", "dSigma", "up%", "1st", "2nd", "3rd", "4th");
        long totN = 0, totUp = 0;
        double totOrd = 0, totMu = 0, totSigma = 0;
        double[] totPos = new double[4];
        long[] totPosN = new long[4];

        for (int r = 0; r < GainStats.R; r++) {
            if (g.n[r] == 0) continue;
            out("%-9s %9d %+8.3f %+8.3f %+8.3f %6.0f |",
                    Rank.values()[r].name(), g.n[r], g.ord[r] / g.n[r], g.mu[r] / g.n[r],
                    g.sigma[r] / g.n[r], 100.0 * g.up[r] / g.n[r]);
            for (int p = 0; p < 4; p++) {
                out(" %+7.2f", g.posN[r][p] == 0 ? Double.NaN : g.posOrd[r][p] / g.posN[r][p]);
                totPos[p] += g.posOrd[r][p];
                totPosN[p] += g.posN[r][p];
            }
            out("%n");
            totN += g.n[r];
            totUp += g.up[r];
            totOrd += g.ord[r];
            totMu += g.mu[r];
            totSigma += g.sigma[r];
        }
        if (totN > 0) {
            out("%-9s %9d %+8.3f %+8.3f %+8.3f %6.0f |",
                    "ALL", totN, totOrd / totN, totMu / totN, totSigma / totN, 100.0 * totUp / totN);
            for (int p = 0; p < 4; p++) {
                out(" %+7.2f", totPosN[p] == 0 ? Double.NaN : totPos[p] / totPosN[p]);
            }
            out("%n");
        }
        out("(dOrd = avg ordinal change per match; up%% = share of matches with a positive gain;%n"
                + " 1st..4th = avg ordinal change by finishing position; rank = rank BEFORE the match)%n");
    }

    private static void printReport(String title, List<Scenario> scenarios, List<Sim> sims) {
        out("%n==================== %s ====================%n", title);

        out("%n-- A) Accuracy: hidden skill vs rating (rho = Spearman, 1.0 = perfect) --%n");
        out("%-30s %7s %7s %9s %7s %7s %8s%n",
                "scenario", "players", "rooms", "games/pl", "rhoOrd", "rhoMu", "top10%");
        for (int i = 0; i < scenarios.size(); i++) {
            Scenario s = scenarios.get(i);
            Sim sim = sims.get(i);
            Metrics m = sim.snapshot();
            out("%-30s %7d %7d %9.1f %7.3f %7.3f %7.0f%%%n",
                    s.name(), s.players(), s.rooms(), sim.avgGames(),
                    m.rhoOrd(), m.rhoMu(), m.topOverlap() * 100);
        }

        out("%n-- B) Inflation and tiers: count (avg hidden skill of that tier) --%n");
        out("%-30s %7s %7s %7s %7s  ", "scenario", "meanMu", "meanSig", "meanOrd", "maxOrd");
        for (Rank r : Rank.values()) out("%-11s", r.name().substring(0, Math.min(6, r.name().length())));
        out("%n");
        for (int i = 0; i < scenarios.size(); i++) {
            Metrics m = sims.get(i).snapshot();
            out("%-30s %7.2f %7.2f %7.2f %7.2f  ",
                    scenarios.get(i).name(), m.meanMu(), m.meanSigma(), m.meanOrd(), m.maxOrd());
            for (int t = 0; t < m.tierCount().length; t++) {
                out("%-11s", tierCell(m.tierCount()[t], m.tierSkill()[t]));
            }
            out("%n");
        }

        out("%n-- D) Rank distribution: players in each rank (share of players with >= 1 match) --%n");
        out("%-30s %6s %7s %7s  ", "scenario", "played", "unplayd", "provis.");
        for (Rank r : Rank.values()) out("%-11s", r.name());
        out("%n");
        for (int i = 0; i < scenarios.size(); i++) {
            Sim sim = sims.get(i);
            int[] counts = sim.rankCounts();
            int played = Arrays.stream(counts).sum();
            out("%-30s %6d %7d %7d  ", scenarios.get(i).name(), played,
                    sim.unplayedCount(), sim.provisionalCount());
            for (int c : counts) {
                double share = played == 0 ? 0 : 100.0 * c / played;
                out("%-11s", String.format(Locale.ROOT, "%d (%.0f%%)", c, share));
            }
            out("%n");
        }
        out("(provis. = played fewer than %d matches, so their rank is capped at %s)%n",
                Rank.MIN_MATCHES_FOR_TOP_TIERS, Rank.GOLD.name());

        out("%n-- C) Finish distribution, 2nd half of rooms (random guess = 25%% each) --%n");
        out("%-30s | %-13s | %-13s | %-13s%n", "scenario",
                "top-rated", "lowest-rated", "best-skill");
        out("%-30s | %-6s %-6s | %-6s %-6s | %-6s %-6s%n", "", "1st%", "4th%", "1st%", "4th%", "1st%", "4th%");
        for (int i = 0; i < scenarios.size(); i++) {
            Sim sim = sims.get(i);
            out("%-30s | %-6s %-6s | %-6s %-6s | %-6s %-6s%n", scenarios.get(i).name(),
                    pct(sim, 0, 0), pct(sim, 0, 3),
                    pct(sim, 1, 0), pct(sim, 1, 3),
                    pct(sim, 2, 0), pct(sim, 2, 3));
        }
        out("(top-rated = highest mu in the room before the match; best-skill = highest hidden skill,%n"
                + " the ceiling no rating system can beat.)%n");

        printGainByRank("E1) Avg ordinal gain per match, by rank before the match (ALL matches)",
                scenarios, sims, false);
        printGainByRank("E2) Avg ordinal gain per match, by rank before the match (2nd half of rooms)",
                scenarios, sims, true);
        out("(a positive number means players of that rank gain ordinal on average;%n"
                + " -- = nobody was in that rank at the start of a match)%n");
    }

    private static void assertSane(Sim sim) {
        for (Rating r : sim.ratings) {
            assertTrue(Double.isFinite(r.mu), "mu must be finite");
            assertTrue(Double.isFinite(r.sigma) && r.sigma > 0, "sigma must be positive and finite");
            assertFalse(r.matches < Rank.MIN_MATCHES_FOR_TOP_TIERS && Rank.of(r).compareTo(Rank.GOLD) > 0,
                    "players with few matches must be capped at GOLD");
        }
    }

    /* ====================================================================== */
    /* Tests                                                                   */
    /* ====================================================================== */

    @Test
    void scenarioGrid() {
        List<Scenario> scenarios = List.of(
                new Scenario("4 players, 4 rooms, mixed", 4, 4, MIXED, DEFAULT_LUCK_SD),
                new Scenario("4 players, 100 rooms, max21", 4, 100, fixed(21), DEFAULT_LUCK_SD),
                new Scenario("10 players, 40 rooms, mixed", 10, 40, MIXED, DEFAULT_LUCK_SD),
                new Scenario("20 players, 100 rooms, mixed", 20, 100, MIXED, DEFAULT_LUCK_SD),
                new Scenario("50 players, 1000 rooms, mixed", 50, 1000, MIXED, DEFAULT_LUCK_SD),
                new Scenario("100 players, 100 rooms, mixed", 100, 100, MIXED, DEFAULT_LUCK_SD),
                new Scenario("100 players, 1000 rooms, mixed", 100, 1000, MIXED, DEFAULT_LUCK_SD),
                new Scenario("100 players, 10000 rooms, mixed", 100, 10_000, MIXED, DEFAULT_LUCK_SD),
                new Scenario("100 players, 50000 rooms, mixed", 100, 50_000, MIXED, DEFAULT_LUCK_SD),
                new Scenario("100 pl, 5000 rooms, max21", 100, 5000, fixed(21), DEFAULT_LUCK_SD),
                new Scenario("100 pl, 5000 rooms, max50", 100, 5000, fixed(50), DEFAULT_LUCK_SD),
                new Scenario("100 pl, 5000 rooms, LOW luck", 100, 5000, MIXED, 0.75),
                new Scenario("100 pl, 5000 rooms, HIGH luck", 100, 5000, MIXED, 3.0));

        List<Sim> sims = runAll("SCENARIO GRID", scenarios, 1000);

        for (int i = 0; i < sims.size(); i++) {
            Sim sim = sims.get(i);
            assertSane(sim);
            Scenario s = scenarios.get(i);
            if ((double) s.rooms() * 4 / s.players() >= 100) {
                assertTrue(sim.snapshot().rhoOrd() > 0.3,
                        "ratings should correlate with hidden skill in: " + s.name());
            }
        }
    }

    /** Only total_max_score = 3 (a single hand decides the room): does it inflate ranks? */
    @Test
    void minimumScoreOnly() {
        List<Scenario> scenarios = List.of(
                new Scenario("max3 only, 500 rooms", 100, 500, fixed(3), DEFAULT_LUCK_SD),
                new Scenario("max3 only, 5000 rooms", 100, 5000, fixed(3), DEFAULT_LUCK_SD),
                new Scenario("max3 only, 50000 rooms", 100, 50_000, fixed(3), DEFAULT_LUCK_SD),
                new Scenario("max21 only, 5000 rooms (ref)", 100, 5000, fixed(21), DEFAULT_LUCK_SD),
                new Scenario("max50 only, 5000 rooms (ref)", 100, 5000, fixed(50), DEFAULT_LUCK_SD));

        for (Sim sim : runAll("MINIMUM SCORE (3) ONLY", scenarios, 2000)) assertSane(sim);
    }

    /** How many rooms are needed before the ranking becomes trustworthy? */
    @Test
    void convergenceOverTime() {
        int[] checkpoints = {10, 50, 100, 250, 500, 1000, 2500, 5000, 10_000, 20_000};
        Sim sim = new Sim(3000, 100, DEFAULT_LUCK_SD, Integer.MAX_VALUE);

        out("%n==================== CONVERGENCE (100 players, mixed max scores) ====================%n");
        out("%7s %9s %7s %7s %8s %7s %7s  %s%n",
                "rooms", "games/pl", "rhoOrd", "rhoMu", "top10%", "meanOrd", "maxOrd", "players per rank");
        for (int cp : checkpoints) {
            while (sim.roomsPlayed < cp) sim.playRoom(MIXED.applyAsInt(sim.rnd));
            Metrics m = sim.snapshot();
            out("%7d %9.1f %7.3f %7.3f %7.0f%% %7.2f %7.2f  %s%n",
                    cp, sim.avgGames(), m.rhoOrd(), m.rhoMu(), m.topOverlap() * 100,
                    m.meanOrd(), m.maxOrd(), rankSummary(sim));
            assertSane(sim);
        }
        printGainDetail("Gain per match by rank, whole convergence run", sim.gainAll);
    }

    @Test
    void smallPoolSweep() {
        int[] poolSizes = {5, 8, 10, 15, 20, 30};
        int[] gamesPerPlayer = {10, 30, 60, 100, 200};
        int seeds = 20;

        out("%n==================== SMALL POOLS (avg of %d seeds, mixed max scores) ====================%n", seeds);
        out("%7s %9s %7s %7s %7s %7s %7s %7s %9s %8s%n",
                "players", "games/pl", "rooms", "rhoOrd", "meanMu", "sdMu", "maxOrd",
                "provis%", "diam+mas%", "gold%");

        for (int players : poolSizes) {
            for (int g : gamesPerPlayer) {
                int rooms = Math.max(1, g * players / 4);
                double rho = 0, meanMu = 0, sdMu = 0, maxOrd = 0, provis = 0, top = 0, gold = 0;
                for (int s = 0; s < seeds; s++) {
                    Sim sim = new Sim(9000 + s, players, DEFAULT_LUCK_SD, Integer.MAX_VALUE);
                    for (int r = 0; r < rooms; r++) sim.playRoom(MIXED.applyAsInt(sim.rnd));
                    assertSane(sim);

                    Metrics m = sim.snapshot();
                    rho += m.rhoOrd();
                    meanMu += m.meanMu();
                    maxOrd += m.maxOrd();

                    double var = 0;
                    for (Rating rt : sim.ratings) var += Math.pow(rt.mu - m.meanMu(), 2);
                    sdMu += Math.sqrt(var / players);

                    int played = Math.max(1, m.rated());
                    provis += 100.0 * sim.provisionalCount() / played;
                    int[] c = sim.rankCounts();
                    top += 100.0 * (c[Rank.DIAMOND.ordinal()] + c[Rank.MASTER.ordinal()]) / played;
                    gold += 100.0 * c[Rank.GOLD.ordinal()] / played;
                }
                out("%7d %9d %7d %7.3f %7.2f %7.2f %7.2f %7.0f %9.0f %8.0f%n",
                        players, g, rooms, rho / seeds, meanMu / seeds, sdMu / seeds,
                        maxOrd / seeds, provis / seeds, top / seeds, gold / seeds);
            }
            out("%n");
        }
    }

    /**
     * How much does a player gain per match, by rank? Averaged over several seeds.
     * Prints a detailed table (per rank, plus split by finishing position) for each setup,
     * for the whole run and for the second half only (closer to steady state).
     */
    @Test
    void gainPerMatchByRank() {
        record Setup(String name, int players, int gamesPerPlayer, ToIntFunction<Random> scores) {}
        List<Setup> setups = List.of(
                new Setup("10 players, 200 games/pl, mixed", 10, 200, MIXED),
                new Setup("20 players, 200 games/pl, mixed", 20, 200, MIXED),
                new Setup("100 players, 200 games/pl, mixed", 100, 200, MIXED),
                new Setup("100 players, 200 games/pl, max21", 100, 200, fixed(21)),
                new Setup("100 players, 200 games/pl, max3", 100, 200, fixed(3)));
        int seeds = 5;

        out("%n==================== GAIN PER MATCH BY RANK (avg of %d seeds) ====================%n", seeds);
        for (Setup st : setups) {
            int rooms = st.gamesPerPlayer() * st.players() / 4;
            GainStats all = new GainStats();
            GainStats late = new GainStats();
            for (int s = 0; s < seeds; s++) {
                Sim sim = new Sim(7000 + s, st.players(), DEFAULT_LUCK_SD, rooms / 2);
                for (int r = 0; r < rooms; r++) sim.playRoom(st.scores().applyAsInt(sim.rnd));
                assertSane(sim);
                all.merge(sim.gainAll);
                late.merge(sim.gainLate);
            }
            printGainDetail(st.name() + " | ALL matches", all);
            printGainDetail(st.name() + " | 2nd half only", late);
        }
    }

    private static String rankSummary(Sim sim) {
        int[] counts = sim.rankCounts();
        StringBuilder sb = new StringBuilder();
        for (Rank r : Rank.values()) {
            if (!sb.isEmpty()) sb.append(" | ");
            sb.append(r.name()).append(' ').append(counts[r.ordinal()]);
        }
        sb.append(" | unplayed ").append(sim.unplayedCount());
        return sb.toString();
    }
}