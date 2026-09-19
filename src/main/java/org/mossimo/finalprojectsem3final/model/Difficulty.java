package org.mossimo.finalprojectsem3final.model;

/**
 * How hard the game is
 *
 * Each level changes three things: how much money you start with, how much
 * you need to reach, and how violently the market moves
 *
 *   Level    Start     Target    Gain needed   Volatility
 *   Easy     $10,000   $13,000       +30%      × 0.75  (calmer)
 *   Normal   $10,000   $15,000       +50%      × 1.00
 *   Hard     $10,000   $20,000      +100%      × 1.40  (wilder)
 *
 * Note that Hard is not simply "harder". A 1.4× volatility multiplier makes
 * a +100% run genuinely possible
 */
public enum Difficulty {

    EASY("Easy",
            10_000.00,
            13_000.00,
            0.75,
            10,
            "A calm market and a modest goal. Good for learning the controls."),

    NORMAL("Normal",
            10_000.00,
            15_000.00,
            1.00,
            10,
            "The standard game. Reach $15,000 in ten trading days."),

    HARD("Hard",
            10_000.00,
            20_000.00,
            1.40,
            10,
            "A violent market and a doubling target. Timing matters.");

    private final String displayName;
    private final double startingCash;
    private final double targetValue;

    /**
     * Multiplies every company's volatility
     *
     * 0.75 makes MedCore's 0.020 become 0.015 and QuantumAI's 0.100 become
     * 0.075. The relative risk between companies is preserved; only the overall
     * intensity changes
     */
    private final double volatilityMultiplier;

    private final int totalDays;
    private final String description;

    Difficulty(String displayName,
               double startingCash,
               double targetValue,
               double volatilityMultiplier,
               int totalDays,
               String description) {
        this.displayName = displayName;
        this.startingCash = startingCash;
        this.targetValue = targetValue;
        this.volatilityMultiplier = volatilityMultiplier;
        this.totalDays = totalDays;
        this.description = description;
    }


