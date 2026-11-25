package org.mossimo.finalprojectsem3final.model;

import org.mossimo.finalprojectsem3final.util.PriceGenerator;

public class Game {

    private final GameConfig config;
    private final PriceGenerator priceGenerator;
    private final Market market;
    private final Portfolio portfolio;
    private final Simulation simulation;

    private Game(GameConfig config, PriceGenerator priceGenerator, Market market,
                 Portfolio portfolio, Simulation simulation) {
        this.config = config;
        this.priceGenerator = priceGenerator;
        this.market = market;
        this.portfolio = portfolio;
        this.simulation = simulation;
    }

    public static Game create(GameConfig config) {
        PriceGenerator generator = new PriceGenerator(config.getSeed());

        Market market = Market.createDefaultMarket(generator, config.getVolatilityMultiplier());
        Portfolio portfolio = new Portfolio(config.getStartingCash());
        Simulation simulation = new Simulation(market, portfolio, config);

        return new Game(config, generator, market, portfolio, simulation);
    }

    public static Game newGame(Difficulty difficulty) {
        return create(GameConfig.standard(difficulty));
    }

    public static Game newGame(Difficulty difficulty, boolean eventsEnabled) {
        return create(GameConfig.standard(difficulty, eventsEnabled));
    }

    public static Game seeded(Difficulty difficulty, long seed) {
        return create(GameConfig.seeded(difficulty, seed));
    }

    public static Game seeded(Difficulty difficulty, long seed, boolean eventsEnabled) {
        return create(GameConfig.seeded(difficulty, seed, eventsEnabled));
    }

    public Game restart() {
        return newGame(config.getDifficulty(), config.isEventsEnabled());
    }

    public GameConfig getConfig() {
        return config;
    }

    public PriceGenerator getPriceGenerator() {
        return priceGenerator;
    }

    public Market getMarket() {
        return market;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public String toString() {
        return "Game[" + config + "] " + simulation;
    }
}
