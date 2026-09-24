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
    @Test
    @DisplayName("tick moves every stock, not just the first")
    void tick_movesEveryStock() {
        Market market = defaultMarket();

        List<Double> before = market.getStocks().stream()
                .map(Stock::getCurrentPrice)
                .toList();

        market.tick();

        List<Stock> after = market.getStocks();

        for (int i = 0; i < after.size(); i++) {
            assertEquals(2, after.get(i).getHistorySize(),
                    after.get(i).getSymbol() + " should have opening price + 1 tick");

            // With real volatility, landing on exactly the same price to the
            // cent would be a one in a million coincidence
            // If this fails for every stock, tick() is not calling the generator
            assertNotEqualsWithinCent(before.get(i), after.get(i).getCurrentPrice(),
                    after.get(i).getSymbol() + " did not move");
        }
    }

    @Test
    @DisplayName("tick returns the change applied to each stock, in order")
    void tick_returnsChangesInOrder() {
        Market market = predictableMarket();

        List<Double> changes = market.tick();

        assertEquals(2, changes.size(),
                "one change per company");

        // RisingCo has trend +0.10 and zero volatility, so exactly +10%
        assertEquals(10.0, changes.get(0), TOLERANCE,
                "first entry should be RisingCo's change");

        // FallingCo has trend -0.05 and zero volatility, so exactly -5%
        assertEquals(-5.0, changes.get(1), TOLERANCE,
                "second entry should be FallingCo's change");

        // The order matches getStocks()
        assertEquals("UP", market.getStocks().get(0).getSymbol());
        assertEquals("DN", market.getStocks().get(1).getSymbol());
    }

    /*
            group 3 - lookups and statistics
     */
    @Test
    @DisplayName("findBySymbol finds a real company and reports a missing one")
    void findBySymbol_findsAndMisses() {
        Market market = defaultMarket();

        Optional<Stock> found = market.findBySymbol("QAI");
        assertTrue(found.isPresent(), "QAI is in the default market");
        assertEquals("QuantumAI", found.get().getCompanyName());

        Optional<Stock> missing = market.findBySymbol("NOPE");
        assertTrue(missing.isEmpty(),
                "a symbol that does not exist must come back empty, not null");

        // Optional forces the caller to deal with it. Calling get() on an empty
        // Optional throws immediately and loudly, instead of handing back null
        // that fails three screens later
        assertThrows(java.util.NoSuchElementException.class, missing::get);
    }

    @Test
    @DisplayName("Best and worst performers compare percentage, not price")
    void bestAndWorstPerformer_comparePercentageNotPrice() {
        Market market = new Market(new PriceGenerator(1L));

        // ExpensiveCo is worth far more per share, but barely grows
        market.addStock(new Stock("EXP", "ExpensiveCo", "Testing", "High price, low growth.",
                500.00, 0.0, 0.01));      // +1% per tick
        // CheapCo costs almost nothing, but grows fast
        market.addStock(new Stock("CHP", "CheapCo", "Testing", "Low price, high growth.",
                10.00, 0.0, 0.08));       // +8% per tick
        market.addStock(new Stock("BAD", "SinkingCo", "Testing", "Going down.",
                50.00, 0.0, -0.04));      // -4% per tick

        for (int i = 0; i < 10; i++) {
            market.tick();
        }

        // CheapCo is worth about $21 and ExpensiveCo about $552, but CheapCo has
        // risen 116% against ExpensiveCo's 10%. Percentage is what matters
        assertEquals("CHP", market.getBestPerformer().orElseThrow().getSymbol(),
                "the best performer is the biggest riser, not the priciest share");

        assertEquals("BAD", market.getWorstPerformer().orElseThrow().getSymbol(),
                "the worst performer is the biggest faller");
    }

    @Test
    @DisplayName("The market index is the average total change across companies")
    void getMarketIndex_averagesTotalPercentChange() {
        Market market = new Market(new PriceGenerator(1L));

        market.addStock(new Stock("A", "AlphaCo", "Testing", "Up.",
                100.00, 0.0, 0.10));    // +10% per tick
        market.addStock(new Stock("B", "BetaCo", "Testing", "Down.",
                100.00, 0.0, -0.10));   // -10% per tick

        // A brand new market has not moved so the index is flat
        assertEquals(0.0, market.getMarketIndex(), TOLERANCE,
                "before any tick the index should be 0%");

        market.tick();

        // AlphaCo is at 110 (+10%), BetaCo at 90 (-10%). Average: 0%
        assertEquals(0.0, market.getMarketIndex(), TOLERANCE,
                "+10% and -10% average out to a flat market");

        assertEquals(0.0, market.getLastTickAverageChange(), TOLERANCE,
                "the same is true of the last tick alone");

        market.tick();

        // AlphaCo 121 (+21%), BetaCo 81 (-19%). Average: +1%
        // The asymmetry is real: compounding gains outrun compounding losses
        assertEquals(1.0, market.getMarketIndex(), 0.01,
                "after two ticks, compounding pushes the index slightly positive");
    }

    /*
            helpers
     */
    /** Asserts two prices differ by at least a cent */
    private void assertNotEqualsWithinCent(double expected, double actual, String message) {
        assertTrue(Math.abs(expected - actual) > 0.01, message);
    }
}
