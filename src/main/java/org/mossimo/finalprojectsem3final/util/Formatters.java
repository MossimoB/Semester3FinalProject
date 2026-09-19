package org.mossimo.finalprojectsem3final.util;

/**
 * Number and text formatting shared by every screen.
 *
 * No JavaFX imports, so this stays testable and could be reused by the
 * console demos (later)
 */
public class Formatters {
    private Formatters() {
    }

    /*
        money
     */

    /**
     * Dollars with a thousands separator and two decimals
     *
     * 1234.5   →  "$1,234.50"
     * -1234.5  →  "-$1,234.50"
     *
     * The minus sign goes before the dollar sign, not after it
     */
    public static String money(double amount) {
        String sign = amount < 0 ? "-" : "";
        return String.format("%s$%,.2f", sign, Math.abs(amount));
    }
}
