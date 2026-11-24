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

}
