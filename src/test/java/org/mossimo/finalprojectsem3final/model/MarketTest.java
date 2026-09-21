package org.mossimo.finalprojectsem3final.model;

import org.mossimo.finalprojectsem3final.util.PriceGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketTest {

    private static final double TOLERANCE = 0.001;

    /** The real six-company market with a fixed seed so runs are repeatable */
    private Market defaultMarket() {
        return Market.createDefaultMarket(new PriceGenerator(42L));
    }

    /**
     * A tiny two-company market used where six companies would only make the arithmetic harder to follow
     * Both have zero volatility, so their movement is entirely controlled by their trend and the test can predict it
     */
    private Market predictableMarket() {
        Market market = new Market(new PriceGenerator(1L));
        market.addStock(new Stock("UP", "RisingCo", "Testing", "Always climbs.",
                100.00, 0.0, 0.10));     // +10% per tick, exactly
        market.addStock(new Stock("DN", "FallingCo", "Testing", "Always drops.",
                100.00, 0.0, -0.05));    // -5% per tick, exactly
        return market;
    }

    /*
            group 1 - default market is set up correctly
     */
    @Test
    @DisplayName("The default market has the six companies from the proposal")
    void createDefaultMarket_hasSixCompanies() {
        Market market = defaultMarket();

        assertEquals(6, market.size(),
                "the proposal promises six companies");

        // Check each one by symbol, so a typo in createDefaultMarket is caught
        // here rather than by a blank row in the table
        for (String symbol : List.of("NVT", "GRG", "MDC", "AER", "FRB", "QAI")) {
            assertTrue(market.findBySymbol(symbol).isPresent(),
                    "missing company: " + symbol);
        }
    }

    @Test
    @DisplayName("Every company has a distinct symbol and sensible settings")
    void createDefaultMarket_companiesAreWellFormed() {
        Market market = defaultMarket();

        Set<String> seenSymbols = new HashSet<>();

        for (Stock stock : market.getStocks()) {
            // Duplicate symbols would break findBySymbol and the Week 12 save
            // file, which keys holdings by symbol.
            assertTrue(seenSymbols.add(stock.getSymbol()),
                    "duplicate symbol: " + stock.getSymbol());

            assertTrue(stock.getStartingPrice() > 0,
                    stock.getSymbol() + " must start above $0");
            assertTrue(stock.getVolatility() > 0,
                    stock.getSymbol() + " must have some volatility or it never moves");
            assertFalse(stock.getCompanyName().isBlank(),
                    stock.getSymbol() + " needs a display name for the table");
            assertFalse(stock.getDescription().isBlank(),
                    stock.getSymbol() + " needs a description for the detail screen");
        }

        // The game design depends on QuantumAI being the wildest and MedCore
        // being the calmest. If someone edits those numbers, this test says so
        double quantumAiVolatility = market.findBySymbol("QAI").orElseThrow().getVolatility();
        double medCoreVolatility = market.findBySymbol("MDC").orElseThrow().getVolatility();

        assertTrue(quantumAiVolatility > medCoreVolatility * 3,
                "QuantumAI should be dramatically riskier than MedCore");
    }

    @Test
    @DisplayName("At least one company declines, so holding everything cannot win")
    void createDefaultMarket_hasADecliningCompany() {
        Market market = defaultMarket();

        boolean someoneIsSinking = market.getStocks().stream()
                .anyMatch(stock -> stock.getTrend() < 0);

        assertTrue(someoneIsSinking,
                "if every company drifts upward, 'buy everything and wait' always "
                        + "wins and the game has no decisions in it");
    }

    /*
            group 2 - what tick does
     */
}
