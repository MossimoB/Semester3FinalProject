package org.mossimo.finalprojectsem3final.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PortfolioTest {

    private static final double TOLERANCE = 0.001;
    private static final double STARTING_CASH = 10_000.00;

    private Stock stock() {
        return new Stock("NVT", "NovaTech", "Technology", "Test.", 100.00, 0.06, 0.002);
    }

    private Portfolio portfolio() {
        return new Portfolio(STARTING_CASH);
    }

    /*
            group 1 - starting state
     */
    @Test
    @DisplayName("A new portfolio is all cash and no profit")
    void newPortfolio_isAllCash() {
        Portfolio portfolio = portfolio();

        assertEquals(STARTING_CASH, portfolio.getCash(), TOLERANCE);
        assertEquals(0.0, portfolio.getHoldingsValue(), TOLERANCE);
        assertEquals(STARTING_CASH, portfolio.getTotalValue(), TOLERANCE);
        assertEquals(0.0, portfolio.getProfitLoss(), TOLERANCE);
        assertEquals(0.0, portfolio.getReturnPercent(), TOLERANCE);
        assertEquals(0.0, portfolio.getInvestedPercent(), TOLERANCE);
        assertEquals(0, portfolio.getTradeCount());
        assertTrue(portfolio.getHoldings().isEmpty());
    }

    /*
            group 2 - starting state
    */

}
