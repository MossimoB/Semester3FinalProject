package org.mossimo.finalprojectsem3final.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    @DisplayName("Buying moves cash into holdings without changing total value")
    void buy_movesCashIntoHoldings() {
        Portfolio portfolio = portfolio();
        Stock stock = stock();

        TradeResult result = portfolio.buy(stock, 20, 1, 9);

        assertTrue(result.isSuccessful(), result.getMessage());
        assertEquals(8000.00, portfolio.getCash(), TOLERANCE, "10000 - (20 x 100)");
        assertEquals(2000.00, portfolio.getHoldingsValue(), TOLERANCE);
        assertEquals(20, portfolio.getSharesOwned("NVT"));

        // The instant you buy, you have not made or lost anything. You have
        // swapped $2,000 of cash for $2,000 of shares.
        assertEquals(STARTING_CASH, portfolio.getTotalValue(), TOLERANCE,
                "buying at the market price is value-neutral");
        assertEquals(0.0, portfolio.getProfitLoss(), TOLERANCE);

        assertEquals(20.0, portfolio.getInvestedPercent(), TOLERANCE, "2000 of 10000");
    }

    @Test
    @DisplayName("Buying more than you can afford is rejected with a reason")
    void buy_rejectedWhenTooExpensive() {
        Portfolio portfolio = portfolio();
        Stock stock = stock();

        TradeResult result = portfolio.buy(stock, 500, 1, 9);   // $50,000

        assertTrue(result.isRejected());
        assertEquals(TradeResult.Failure.INSUFFICIENT_FUNDS, result.getFailure());
        assertNull(result.getTransaction(), "a rejected trade produces no Transaction");

        assertEquals(STARTING_CASH, portfolio.getCash(), TOLERANCE);
        assertEquals(0, portfolio.getSharesOwned("NVT"));
        assertEquals(0, portfolio.getTradeCount(), "a rejected trade is not history");
    }

    @Test
    @DisplayName("Zero and negative quantities are rejected before the cash check")
    void buy_rejectsBadQuantity() {
        Portfolio portfolio = portfolio();
        Stock stock = stock();

        assertEquals(TradeResult.Failure.INVALID_QUANTITY,
                portfolio.buy(stock, 0, 1, 9).getFailure());
        assertEquals(TradeResult.Failure.INVALID_QUANTITY,
                portfolio.buy(stock, -5, 1, 9).getFailure());

        assertEquals(STARTING_CASH, portfolio.getCash(), TOLERANCE);
    }

    @Test
    @DisplayName("You can spend your last dollar but not a cent more")
    void buy_exactlyAffordable_succeeds() {
        Portfolio portfolio = portfolio();
        Stock stock = stock();

        // getMaxAffordable floors because you cannot buy part of a share
        assertEquals(100, portfolio.getMaxAffordable(stock), "10000 / 100");

        assertTrue(portfolio.buy(stock, 100, 1, 9).isSuccessful(),
                "spending exactly the balance must be allowed");
        assertEquals(0.0, portfolio.getCash(), TOLERANCE);

        assertEquals(TradeResult.Failure.INSUFFICIENT_FUNDS,
                portfolio.buy(stock, 1, 1, 10).getFailure());
    }

    /*
            group 3 - selling
     */

    @Test
    @DisplayName("Selling at a higher price turns paper profit into cash")
    void sell_realisesProfit() {
        Portfolio portfolio = portfolio();
        Stock stock = stock();

        portfolio.buy(stock, 20, 1, 9);      // $2,000 at $100
        stock.setPrice(120.00);

        // Before selling: $400 of UNREALISED profit
        assertEquals(400.00, portfolio.getUnrealisedProfit(), TOLERANCE);
        assertEquals(0.0, portfolio.getRealisedProfit(), TOLERANCE);

        TradeResult result = portfolio.sell(stock, 20, 2, 11);

        assertTrue(result.isSuccessful(), result.getMessage());
        assertEquals(10_400.00, portfolio.getCash(), TOLERANCE, "8000 + (20 x 120)");
        assertEquals(0, portfolio.getSharesOwned("NVT"));

        // Now it is REALISED and can never be lost again
        assertEquals(400.00, portfolio.getRealisedProfit(), TOLERANCE);
        assertEquals(0.0, portfolio.getUnrealisedProfit(), TOLERANCE, "nothing is held any more");
        assertEquals(400.00, portfolio.getProfitLoss(), TOLERANCE);
        assertEquals(4.00, portfolio.getReturnPercent(), TOLERANCE, "400 / 10000");

        Transaction sale = result.getTransaction();
        assertNotNull(sale);
        assertEquals(400.00, sale.getRealisedProfit(), TOLERANCE);
        assertTrue(sale.isProfitable());
    }


    @Test
    @DisplayName("Selling shares you do not own is rejected")
    void sell_rejectedWhenNotEnoughShares() {
        Portfolio portfolio = portfolio();
        Stock stock = stock();

        // Owning none at all
        TradeResult none = portfolio.sell(stock, 5, 1, 9);
        assertEquals(TradeResult.Failure.INSUFFICIENT_SHARES, none.getFailure());

        // Owning some but not enough
        portfolio.buy(stock, 10, 1, 9);
        TradeResult tooMany = portfolio.sell(stock, 11, 1, 10);
        assertEquals(TradeResult.Failure.INSUFFICIENT_SHARES, tooMany.getFailure());

        assertEquals(10, portfolio.getSharesOwned("NVT"), "nothing should have been sold");
    }

    @Test
    @DisplayName("Selling everything removes the position from the table")
    void sell_allShares_removesHolding() {
        Portfolio portfolio = portfolio();
        Stock stock = stock();

        portfolio.buy(stock, 10, 1, 9);
        assertEquals(1, portfolio.getHoldings().size());

        portfolio.sell(stock, 10, 1, 10);

        assertTrue(portfolio.getHoldings().isEmpty(),
                "a position with 0 shares must not appear in the portfolio table");
        assertNull(portfolio.getHolding("NVT"));
    }

    /*
            group 4 - the two together
     */


}
