package de.alexanderhofmeister.rechnungen.util;

import java.math.BigDecimal;

public class MoneyUtil {

    public static String format(final BigDecimal value) {
        return String.format("%.2f €", value);
    }


    public static BigDecimal convertToBigDecimal(String value) {
        value = value.replaceAll(",", ".").replaceAll("€", "").trim();
        if (value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }
}
