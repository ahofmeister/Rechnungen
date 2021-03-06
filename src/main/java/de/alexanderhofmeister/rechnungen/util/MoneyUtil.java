package de.alexanderhofmeister.rechnungen.util;

import java.math.BigDecimal;

public class MoneyUtil {

    public static String toCurrencyWithSymbol(final BigDecimal value) {
        return String.format("%.2f €", value);
    }


    public static BigDecimal convertToBigDecimal(String value) {
        value = convertToGermanCurrency(value);
        if (value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }

    public static String convertToGermanCurrency(String value) {
        return value.replaceAll(",", ".").replaceAll("€", "").trim();
    }

}
