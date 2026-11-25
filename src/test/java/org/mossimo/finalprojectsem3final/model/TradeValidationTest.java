package org.mossimo.finalprojectsem3final.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TradeValidationTest {

    private static final double TOLERANCE = 0.001;

    private Game game() {
        return Game.seeded(Difficulty.NORMAL, 84L);
    }

    /*
    REJECTIONS
     */

    @Test
    @DisplayName("Every rejection reason produces the right Failure enum")
    void everyRejectionReason_isReported() {
        Game game = game();
        Portfolio portfolio = game.getPortfolio();
        Stock stock = game.getMarket().findBySymbol("NVT").orElseThrow();

        // Zero and negative quantities.
        assertEquals(TradeResult.Failure.INVALID_QUANTITY,
                portfolio.buy(stock, 0, 1, 9).getFailure());
        assertEquals(TradeResult.Failure.INVALID_QUANTITY,
                portfolio.sell(stock, -3, 1, 9).getFailure());

        // Cannot afford it. $10,000 will not buy 500 shares at $100.
        assertEquals(TradeResult.Failure.INSUFFICIENT_FUNDS,
                portfolio.buy(stock, 500, 1, 9).getFailure());

        // Does not own any.
        assertEquals(TradeResult.Failure.INSUFFICIENT_SHARES,
                portfolio.sell(stock, 1, 1, 9).getFailure());
    }

    @Test
    @DisplayName("A rejected trade leaves the account exactly as it was")
    void rejectedTrade_changesNothing() {
        Game game = game();
        Portfolio portfolio = game.getPortfolio();
        Stock stock = game.getMarket().findBySymbol("NVT").orElseThrow();

        double cashBefore = portfolio.getCash();
        int tradesBefore = portfolio.getTradeCount();

        portfolio.buy(stock, 999999, 1, 9);     // far too expensive
        portfolio.buy(stock, -5, 1, 9);         // nonsense quantity
        portfolio.sell(stock, 10, 1, 9);        // owns none

        assertEquals(cashBefore, portfolio.getCash(), TOLERANCE,
                "a rejected trade must not move any money");
        assertEquals(tradesBefore, portfolio.getTradeCount(),
                "a rejected trade must not appear in the history");
        assertTrue(portfolio.getHoldings().isEmpty(),
                "a rejected trade must not create a holding");
    }

    @Test
    @DisplayName("Quantity is checked before affordability, so the message is useful")
    void quantityIsCheckedFirst() {
        Portfolio broke = new Portfolio(0.0);
        Game game = game();
        Stock stock = game.getMarket().findBySymbol("NVT").orElseThrow();

        // This player has no money AND asked for a nonsense quantity. Telling
        // them "not enough cash" would be technically true and unhelpful: the
        // actual problem is the -5.
        TradeResult result = broke.buy(stock, -5, 1, 9);

        assertEquals(TradeResult.Failure.INVALID_QUANTITY, result.getFailure(),
                "the nonsense quantity is the more useful thing to report");
    }

    /*
    PLAYER MESSAGE
     */
    @Test
    @DisplayName("Rejection messages name the actual numbers")
    void rejectionMessages_areSpecific() {
        Game game = game();
        Portfolio portfolio = game.getPortfolio();
        Stock stock = game.getMarket().findBySymbol("NVT").orElseThrow();

        String message = portfolio.buy(stock, 500, 1, 9).getMessage();

        // "Trade failed" would be useless. The message has to say what it costs
        // and what they have.
        assertTrue(message.contains("50,000"),
                "should name the cost, was: " + message);
        assertTrue(message.contains("10,000"),
                "should name the available cash, was: " + message);

        portfolio.buy(stock, 10, 1, 9);
        String sellMessage = portfolio.sell(stock, 25, 1, 10).getMessage();

        assertTrue(sellMessage.contains("25") && sellMessage.contains("10"),
                "should name both the requested and owned amounts, was: " + sellMessage);
        assertTrue(sellMessage.contains("NVT"), "should name the company");
    }

    @Test
    @DisplayName("A successful trade carries a Transaction, a rejected one does not")
    void successCarriesTheTransaction() {
        Game game = game();
        Portfolio portfolio = game.getPortfolio();
        Stock stock = game.getMarket().findBySymbol("NVT").orElseThrow();

        TradeResult good = portfolio.buy(stock, 10, 3, 14);
        assertTrue(good.isSuccessful());
        assertNotNull(good.getTransaction());
        assertEquals(Transaction.Type.BUY, good.getTransaction().getType());
        assertEquals(10, good.getTransaction().getShares());
        assertEquals(3, good.getTransaction().getSimulationDay());
        assertEquals(14, good.getTransaction().getSimulationHour());
        assertEquals(TradeResult.Failure.NONE, good.getFailure());

        TradeResult bad = portfolio.buy(stock, 999999, 3, 14);
        assertTrue(bad.isRejected());
        assertNull(bad.getTransaction(),
                "there is no transaction, because nothing happened");
    }

    /*
    MAX BUTTON
     */
    @Test
    @DisplayName("getMaxAffordable never returns a quantity that would be rejected")
    void maxAffordable_isAlwaysBuyable() {
        Game game = game();
        Portfolio portfolio = game.getPortfolio();

        // Check against every company, including the $150 one and the $40 one.
        for (Stock stock : game.getMarket().getStocks()) {
            Portfolio fresh = new Portfolio(10_000.00);
            int max = fresh.getMaxAffordable(stock);

            assertTrue(fresh.buy(stock, max, 1, 9).isSuccessful(),
                    "the Max button offered " + max + " shares of " + stock.getSymbol()
                            + " but the trade was rejected");

            // And one more share must not be affordable, or Max was too low.
            assertEquals(TradeResult.Failure.INSUFFICIENT_FUNDS,
                    fresh.buy(stock, 1, 1, 9).getFailure(),
                    "Max should have spent everything it could for " + stock.getSymbol());
        }
    }
}
