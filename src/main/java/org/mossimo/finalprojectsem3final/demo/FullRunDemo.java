package org.mossimo.finalprojectsem3final.demo;

import org.mossimo.finalprojectsem3final.model.Difficulty;
import org.mossimo.finalprojectsem3final.model.Game;
import org.mossimo.finalprojectsem3final.model.Holding;
import org.mossimo.finalprojectsem3final.model.Portfolio;
import org.mossimo.finalprojectsem3final.model.Simulation;
import org.mossimo.finalprojectsem3final.model.Stock;

public class FullRunDemo {

    private static final long SEED = 84L;

    public static void main(String[] args) {

        System.out.println();
        System.out.println("  STOCKSIM  ·  FULL RUN DEMO");
        System.out.println("  The complete engine, all three difficulties, no interface.");
        System.out.printf("  Seed %d. Strategy: split cash evenly across all six companies on day 1.%n%n", SEED);

        for (Difficulty difficulty : Difficulty.values()) {
            playOneGame(difficulty);
        }

        System.out.println("  The engine is finished. Week 5 puts a window on top of it");
        System.out.println("  and adds no rules whatsoever.");
        System.out.println();
    }

    private static void playOneGame(Difficulty difficulty) {

        Game game = Game.seeded(difficulty, SEED);
        Simulation simulation = game.getSimulation();
        Portfolio portfolio = game.getPortfolio();

        System.out.printf("  %s  —  start $%,.0f, target $%,.0f (%+.0f%% needed), volatility x%.2f%n",
                difficulty.getDisplayName().toUpperCase(),
                difficulty.getStartingCash(),
                difficulty.getTargetValue(),
                difficulty.getRequiredGainPercent(),
                difficulty.getVolatilityMultiplier());

        simulation.start();

        double budgetEach = portfolio.getCash() / game.getMarket().size();
        for (Stock stock : game.getMarket().getStocks()) {
            int quantity = (int) (budgetEach / stock.getCurrentPrice());
            portfolio.buy(stock, quantity, 1, 9);
        }

        System.out.printf("    Day  1  bought %d positions, $%,.2f cash left%n",
                portfolio.getHoldings().size(), portfolio.getCash());

        while (!simulation.isFinished()) {
            simulation.tick();
        }

        System.out.printf("    Day %2d  final value $%,.2f%n",
                simulation.getCurrentDay(), portfolio.getTotalValue());

        for (Holding holding : portfolio.getHoldings()) {
            System.out.printf("            %-4s %4d shares   %+8.2f%%%n",
                    holding.getSymbol(), holding.getShares(), holding.getReturnPercent());
        }

        System.out.printf("    Return %+.2f%%   Market index %+.2f%%   Peak $%,.2f%n",
                portfolio.getReturnPercent(),
                game.getMarket().getMarketIndex(),
                simulation.getPeakValue());

        System.out.printf("    %s   %s%n",
                simulation.hasWon() ? "TARGET REACHED" : "target missed",
                simulation.beatTheMarket() ? "beat the market" : "lost to the market");

        System.out.println();
    }
}
