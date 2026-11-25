package org.mossimo.finalprojectsem3final.model;

public class GameConfig {

    public static final int DEFAULT_MARKET_OPEN_HOUR = 9;

    public static final int DEFAULT_MARKET_CLOSE_HOUR = 16;

    private final Difficulty difficulty;
    private final int totalDays;
    private final int marketOpenHour;
    private final int marketCloseHour;
    private final long seed;
    private final boolean eventsEnabled;

    public GameConfig(Difficulty difficulty, int totalDays,
                      int marketOpenHour, int marketCloseHour,
                      long seed, boolean eventsEnabled) {
        this.difficulty = difficulty;
        this.totalDays = totalDays;
        this.marketOpenHour = marketOpenHour;
        this.marketCloseHour = marketCloseHour;
        this.seed = seed;
        this.eventsEnabled = eventsEnabled;
    }

    public static GameConfig standard(Difficulty difficulty) {
        return standard(difficulty, true);
    }

    public static GameConfig standard(Difficulty difficulty, boolean eventsEnabled) {
        return new GameConfig(difficulty, difficulty.getTotalDays(),
                DEFAULT_MARKET_OPEN_HOUR, DEFAULT_MARKET_CLOSE_HOUR,
                System.nanoTime(), eventsEnabled);
    }

    public static GameConfig seeded(Difficulty difficulty, long seed) {
        return seeded(difficulty, seed, true);
    }

    public static GameConfig seeded(Difficulty difficulty, long seed, boolean eventsEnabled) {
        return new GameConfig(difficulty, difficulty.getTotalDays(),
                DEFAULT_MARKET_OPEN_HOUR, DEFAULT_MARKET_CLOSE_HOUR,
                seed, eventsEnabled);
    }

    public int getHoursPerDay() {
        return marketCloseHour - marketOpenHour + 1;
    }

    public int getTotalTicks() {
        return totalDays * getHoursPerDay();
    }

    public double getStartingCash() {
        return difficulty.getStartingCash();
    }

    public double getTargetValue() {
        return difficulty.getTargetValue();
    }

    public double getVolatilityMultiplier() {
        return difficulty.getVolatilityMultiplier();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public int getMarketOpenHour() {
        return marketOpenHour;
    }

    public int getMarketCloseHour() {
        return marketCloseHour;
    }

    public long getSeed() {
        return seed;
    }

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    @Override
    public String toString() {
        return String.format("%s, %d days, $%,.0f to $%,.0f, seed %d",
                difficulty.getDisplayName(), totalDays,
                getStartingCash(), getTargetValue(), seed);
    }
}
