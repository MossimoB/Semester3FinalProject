package org.mossimo.finalprojectsem3final.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationTest {

    private static final double TOLERANCE = 0.001;

    private Game game() {
        return Game.seeded(Difficulty.NORMAL, 84L);
    }

    private int playToEnd(Simulation simulation) {
        simulation.start();
        int ticks = 0;
        while (!simulation.isFinished() && ticks < 10_000) {
            simulation.tick();
            ticks++;
        }
        return ticks;
    }

    @Test
    @DisplayName("A new simulation is ready but not running")
    void newSimulation_isNotStarted() {
        Simulation simulation = game().getSimulation();

        assertEquals(SimulationState.NOT_STARTED, simulation.getState());
        assertEquals(1, simulation.getCurrentDay());
        assertEquals(9, simulation.getCurrentHour(), "opens at 09:00");
        assertEquals("Day 1 / 10   09:00", simulation.getClockLabel());
        assertEquals(0.0, simulation.getProgress(), TOLERANCE);
        assertFalse(simulation.isFinished());
    }

    @Test
    @DisplayName("tick does nothing until the simulation is started")
    void tick_ignoredWhileNotRunning() {
        Simulation simulation = game().getSimulation();

        assertFalse(simulation.tick(), "a tick before start() must not happen");
        assertEquals(9, simulation.getCurrentHour(), "the clock must not have moved");

        simulation.start();
        assertTrue(simulation.tick());
        assertEquals(10, simulation.getCurrentHour());

        simulation.pause();
        assertFalse(simulation.tick(), "a tick while paused must not happen");
        assertEquals(10, simulation.getCurrentHour());
    }

    @Test
    @DisplayName("The state machine allows exactly the right transitions")
    void stateMachine_allowsCorrectTransitions() {
        Simulation simulation = game().getSimulation();

        assertTrue(simulation.getState().canStart());
        assertFalse(simulation.getState().canPause());

        simulation.start();
        assertEquals(SimulationState.RUNNING, simulation.getState());
        assertTrue(simulation.getState().isTicking());

        simulation.pause();
        assertEquals(SimulationState.PAUSED, simulation.getState());
        assertFalse(simulation.getState().isTicking());

        assertTrue(simulation.getState().canTrade());

        simulation.togglePause();
        assertEquals(SimulationState.RUNNING, simulation.getState());

        playToEnd(simulation);
        assertEquals(SimulationState.FINISHED, simulation.getState());
        assertFalse(simulation.getState().canTrade(), "the market is closed once finished");
        assertFalse(simulation.getState().canStart(), "FINISHED is terminal");
    }

    @Test
    @DisplayName("A day is eight ticks and rolls over at 16:00")
    void clock_rollsOverAfterMarketClose() {
        Simulation simulation = game().getSimulation();
        simulation.start();

        for (int i = 0; i < 7; i++) {
            simulation.tick();
        }
        assertEquals(1, simulation.getCurrentDay());
        assertEquals(16, simulation.getCurrentHour(), "still day 1 at the close");

        simulation.tick();
        assertEquals(2, simulation.getCurrentDay());
        assertEquals(9, simulation.getCurrentHour(), "day 2 opens at 09:00");

        assertEquals(8, simulation.getConfig().getHoursPerDay(),
                "09:00 to 16:00 inclusive is 8 ticks, not 7");
    }

    @Test
    @DisplayName("A ten-day game is exactly 80 ticks")
    void fullGame_isEightyTicks() {
        Simulation simulation = game().getSimulation();

        assertEquals(80, simulation.getConfig().getTotalTicks());

        int ticks = playToEnd(simulation);

        assertEquals(80, ticks, "10 days x 8 hours");
        assertTrue(simulation.isFinished());
        assertEquals(10, simulation.getCurrentDay(), "must not run past day 10");
        assertEquals(16, simulation.getCurrentHour(), "ends at the closing bell");
        assertEquals(1.0, simulation.getProgress(), TOLERANCE);
        assertEquals(0, simulation.getTicksRemaining());
    }

    @Test
    @DisplayName("The daily value history records one entry per completed day")
    void dailyValueHistory_recordsEachClose() {
        Game game = game();
        Simulation simulation = game.getSimulation();

        playToEnd(simulation);

        assertEquals(10, simulation.getDailyValueHistory().size(),
                "one snapshot per day, taken at the closing bell");

        for (double value : simulation.getDailyValueHistory()) {
            assertEquals(10_000.00, value, TOLERANCE,
                    "an untouched portfolio is all cash and never changes value");
        }

        assertEquals(10_000.00, simulation.getPeakValue(), TOLERANCE);
    }

    @Test
    @DisplayName("Doing nothing loses: cash alone never reaches the target")
    void doingNothing_loses() {
        Game game = game();
        Simulation simulation = game.getSimulation();

        playToEnd(simulation);

        assertEquals(10_000.00, game.getPortfolio().getTotalValue(), TOLERANCE);
        assertFalse(simulation.hasWon(), "$10,000 does not reach the $15,000 target");
        assertEquals(5_000.00, simulation.getAmountToTarget(), TOLERANCE);
    }

    @Test
    @DisplayName("Reaching the target wins, and beating the market is separate")
    void winning_andBeatingTheMarket_areDifferentQuestions() {
        Game game = Game.seeded(Difficulty.NORMAL, 84L);
        Simulation simulation = game.getSimulation();
        Portfolio portfolio = game.getPortfolio();

        Stock novaTech = game.getMarket().findBySymbol("NVT").orElseThrow();
        simulation.start();
        portfolio.buy(novaTech, 100, 1, 9);

        playToEnd(simulation);

        assertTrue(simulation.hasWon(),
                "100 shares of the best performer should clear $15,000, got $"
                        + portfolio.getTotalValue());

        assertTrue(simulation.beatTheMarket(),
                "holding the best performer should beat the index");
    }

    @Test
    @DisplayName("Difficulty changes the target, the volatility, and the required gain")
    void difficulty_changesTheRules() {
        assertEquals(30.0, Difficulty.EASY.getRequiredGainPercent(), TOLERANCE);
        assertEquals(50.0, Difficulty.NORMAL.getRequiredGainPercent(), TOLERANCE);
        assertEquals(100.0, Difficulty.HARD.getRequiredGainPercent(), TOLERANCE);

        Game easy = Game.seeded(Difficulty.EASY, 84L);
        Game hard = Game.seeded(Difficulty.HARD, 84L);

        assertEquals(13_000.00, easy.getSimulation().getTargetValue(), TOLERANCE);
        assertEquals(20_000.00, hard.getSimulation().getTargetValue(), TOLERANCE);

        double easyQai = easy.getMarket().findBySymbol("QAI").orElseThrow().getVolatility();
        double hardQai = hard.getMarket().findBySymbol("QAI").orElseThrow().getVolatility();

        assertEquals(0.075, easyQai, TOLERANCE, "0.100 x 0.75");
        assertEquals(0.140, hardQai, TOLERANCE, "0.100 x 1.40");
        assertTrue(hardQai > easyQai, "Hard must be a wilder market");

        double easyMdc = easy.getMarket().findBySymbol("MDC").orElseThrow().getVolatility();
        double hardMdc = hard.getMarket().findBySymbol("MDC").orElseThrow().getVolatility();
        assertEquals(easyQai / easyMdc, hardQai / hardMdc, TOLERANCE,
                "scaling must not change which company is riskiest");
    }
}
