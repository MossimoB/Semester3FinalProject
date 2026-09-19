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

    /**
     * Money with an explicit + or - sign, for profit and loss columns
     *
     * 400.0   →  "+$400.00"
     * -30.0   →  "-$30.00"
     */
    public static String signedMoney(double amount) {
        String sign = amount < 0 ? "-" : "+";
        return String.format("%s$%,.2f", sign, Math.abs(amount));
    }

    /**
     * Money rounded to whole dollars (used where space is tight)
     *
     * 15000.0  →  "$15,000"
     */
    public static String wholeMoney(double amount) {
        String sign = amount < 0 ? "-" : "";
        return String.format("%s$%,.0f", sign, Math.abs(amount));
    }

    /*
            percentages
     */

    /**
     * A percentage with an explicit sign
     *
     * 2.4    →  "+2.40%"
     * -1.1   →  "-1.10%"
     *  0.0   →  "+0.00%"
     *
     * {@code %+.2f} handles the sign automatically, including keeping the
     * plus on zero, which keeps a column of numbers aligned
     */
    public static String percent(double value) {
        return String.format("%+.2f%%", value);
    }

    /**
     * A percentage with no sign, for things that cannot be negative
     * */
    public static String plainPercent(double value) {
        return String.format("%.1f%%", value);
    }

    /*
            shares and counts
     */

    /**
     * A share count, or an em dash if zero
     *
     * A column of zeros is visually messy. "–" reads as "nothing here" and lets
     * the eye skip straight to the rows that matter
     */
    public static String shares(int count) {
        return count == 0 ? "–" : String.format("%,d", count);
    }
}
