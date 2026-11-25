package org.mossimo.finalprojectsem3final.model;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private final Market market;
    private final Portfolio portfolio;
    private final GameConfig config;

    private SimulationState state = SimulationState.NOT_STARTED;

    private int currentDay = 1;
    private int currentHour;

    private final List<Double> dailyValueHistory = new ArrayList<>();

    private double peakValue;

    public Simulation(Market market, Portfolio portfolio, GameConfig config) {
        this.market = market;
        this.portfolio = portfolio;
        this.config = config;
        this.currentHour = config.getMarketOpenHour();
        this.peakValue = portfolio.getTotalValue();
    }

    public void start() {
        if (state.canStart()) {
            state = SimulationState.RUNNING;
        }
    }

    public void pause() {
        if (state.canPause()) {
            state = SimulationState.PAUSED;
        }
    }

    public void resume() {
        start();
    }

    public void togglePause() {
        if (state.isTicking()) {
            pause();
        } else {
            start();
        }
    }

    public boolean tick() {
        if (!state.isTicking()) {
            return false;
        }

        market.tick();

        currentHour++;

        if (currentHour > config.getMarketCloseHour()) {
            recordDailyClose();
            currentDay++;
            currentHour = config.getMarketOpenHour();
        }

        if (currentDay > config.getTotalDays()) {
            currentDay = config.getTotalDays();
            currentHour = config.getMarketCloseHour();
            state = SimulationState.FINISHED;
        }

        return true;
    }

    private void recordDailyClose() {
        double value = portfolio.getTotalValue();
        dailyValueHistory.add(value);
        if (value > peakValue) {
            peakValue = value;
        }
    }

    public boolean isFinished() {
        return state.isOver();
    }

    public boolean hasWon() {
        return portfolio.getTotalValue() >= config.getTargetValue();
    }

    public boolean beatTheMarket() {
        return portfolio.getReturnPercent() > market.getMarketIndex();
    }

    public int getTicksElapsed() {
        int hoursPerDay = config.getHoursPerDay();
        return (currentDay - 1) * hoursPerDay + (currentHour - config.getMarketOpenHour());
    }

    public double getProgress() {
        if (isFinished() || config.getTotalTicks() == 0) {
            return 1.0;
        }
        return Math.min(1.0, (double) getTicksElapsed() / config.getTotalTicks());
    }

    public int getTicksRemaining() {
        if (isFinished()) {
            return 0;
        }
        return Math.max(0, config.getTotalTicks() - getTicksElapsed());
    }

    public double getAmountToTarget() {
        return Math.max(0, config.getTargetValue() - portfolio.getTotalValue());
    }

    public String getClockLabel() {
        return String.format("Day %d / %d   %02d:00",
                currentDay, config.getTotalDays(), currentHour);
    }

    public String getStateLabel() {
        return state.getDisplayName();
    }

    public Market getMarket() {
        return market;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public GameConfig getConfig() {
        return config;
    }

    public SimulationState getState() {
        return state;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public int getCurrentHour() {
        return currentHour;
    }

    public double getTargetValue() {
        return config.getTargetValue();
    }

    public List<Double> getDailyValueHistory() {
        return List.copyOf(dailyValueHistory);
    }

    public double getPeakValue() {
        return peakValue;
    }

    public void restoreClock(int day, int hour, SimulationState state) {
        this.currentDay = day;
        this.currentHour = hour;
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format("%s  %s  $%,.2f / $%,.2f",
                getClockLabel(), state, portfolio.getTotalValue(), config.getTargetValue());
    }
}
